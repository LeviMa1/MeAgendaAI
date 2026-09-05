package br.com.levima.agenda.service;

import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.model.StatusAgendamento;
import br.com.levima.agenda.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    private static final List<StatusAgendamento> STATUS_ATIVOS =
            List.of(StatusAgendamento.CONFIRMADO, StatusAgendamento.PENDENTE);

    private final AgendamentoRepository agendamentoRepository;
    private final AdminService adminService;
    private final CatalogoService catalogoService;

    @Value("${app.agendamento.max-por-semana:3}")
    private int maxPorSemana;

    @Value("${app.agendamento.antecedencia-dias:1}")
    private int antecedenciaDias;

    public AgendamentoService(AgendamentoRepository agendamentoRepository,
                              AdminService adminService,
                              CatalogoService catalogoService) {
        this.agendamentoRepository = agendamentoRepository;
        this.adminService = adminService;
        this.catalogoService = catalogoService;
    }

    @Transactional
    public Agendamento criarAgendamento(String emailUsuario, String nomeUsuario, LocalDate data,
                                        LocalTime horario, String servicoId, String descricao) {
        var servico = catalogoService.validarServico(servicoId);
        validarRegrasAgendamento(emailUsuario, data, horario, null);

        agendamentoRepository.findAtivoComLock(data, horario, STATUS_ATIVOS).ifPresent(a -> {
            throw new IllegalStateException("Esse horario ja esta ocupado. Escolha outro.");
        });

        Agendamento agendamento = new Agendamento(
                emailUsuario, nomeUsuario, data, horario, servico.getId(), servico.getNome(), descricao);
        return agendamentoRepository.save(agendamento);
    }

    @Transactional
    public Agendamento editarAgendamento(String id, String emailUsuario, LocalDate novaData,
                                         LocalTime novoHorario, String servicoId, String descricao) {
        var servico = catalogoService.validarServico(servicoId);
        Agendamento agendamento = getAgendamentoPorId(id);
        if (agendamento == null || !agendamento.getEmailUsuario().equals(emailUsuario)) {
            throw new IllegalArgumentException("Agendamento nao encontrado.");
        }
        if (agendamento.getStatus() == StatusAgendamento.CANCELADO) {
            throw new IllegalStateException("Agendamento cancelado nao pode ser editado.");
        }

        validarRegrasAgendamento(emailUsuario, novaData, novoHorario, id);

        if (!agendamento.getData().equals(novaData) || !agendamento.getHorario().equals(novoHorario)) {
            agendamentoRepository.findAtivoComLock(novaData, novoHorario, STATUS_ATIVOS).ifPresent(a -> {
                if (!a.getId().equals(id)) {
                    throw new IllegalStateException("Esse horario ja esta ocupado. Escolha outro.");
                }
            });
        }

        agendamento.setData(novaData);
        agendamento.setHorario(novoHorario);
        agendamento.setServicoId(servico.getId());
        agendamento.setServicoNome(servico.getNome());
        agendamento.setDescricao(descricao != null ? descricao : "");
        agendamento.setStatus(StatusAgendamento.PENDENTE);
        return agendamentoRepository.save(agendamento);
    }

    private void validarRegrasAgendamento(String emailUsuario, LocalDate data, LocalTime horario, String ignorarId) {
        LocalDate minima = LocalDate.now().plusDays(antecedenciaDias);
        if (!data.isAfter(LocalDate.now()) || data.isBefore(minima)) {
            throw new IllegalArgumentException("A data deve ser futura com antecedencia minima de " + antecedenciaDias + " dia(s).");
        }

        List<String> horariosConfigurados = adminService.getHorariosPorData(data);
        if (horariosConfigurados.isEmpty()) {
            throw new IllegalArgumentException("Data nao disponivel para agendamento.");
        }
        String horarioStr = horario.toString().length() == 5 ? horario.toString() : horario.toString().substring(0, 5);
        if (!horariosConfigurados.contains(horarioStr)) {
            throw new IllegalArgumentException("Horario nao disponivel para esta data.");
        }

        LocalDate inicioSemana = data.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate fimSemana = data.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        long agendamentosSemana = agendamentoRepository.countByEmailUsuarioAndDataBetweenAndStatusIn(
                emailUsuario, inicioSemana, fimSemana, STATUS_ATIVOS);
        if (ignorarId == null && agendamentosSemana >= maxPorSemana) {
            throw new IllegalStateException("Limite de " + maxPorSemana + " agendamentos por semana atingido.");
        }
    }

    public Page<Agendamento> getAgendamentosUsuario(String emailUsuario, Pageable pageable) {
        return agendamentoRepository.findByEmailUsuarioOrderByDataDescHorarioDesc(emailUsuario, pageable);
    }

    public List<Agendamento> getAgendamentosUsuario(String emailUsuario) {
        return agendamentoRepository.findByEmailUsuarioOrderByDataDescHorarioDesc(emailUsuario);
    }

    public Agendamento getAgendamentoPorId(String id) {
        return agendamentoRepository.findById(id).orElse(null);
    }

    @Transactional
    public boolean cancelarAgendamento(String id) {
        Agendamento agendamento = getAgendamentoPorId(id);
        if (agendamento != null) {
            agendamento.setStatus(StatusAgendamento.CANCELADO);
            agendamentoRepository.save(agendamento);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean confirmarAgendamento(String id) {
        Agendamento agendamento = getAgendamentoPorId(id);
        if (agendamento == null || agendamento.getStatus() != StatusAgendamento.PENDENTE) {
            return false;
        }
        agendamentoRepository.findAtivoComLock(agendamento.getData(), agendamento.getHorario(),
                List.of(StatusAgendamento.CONFIRMADO)).ifPresent(a -> {
            throw new IllegalStateException("Horario ja confirmado para outro cliente.");
        });
        agendamento.setStatus(StatusAgendamento.CONFIRMADO);
        agendamentoRepository.save(agendamento);
        return true;
    }

    @Transactional
    public boolean rejeitarAgendamento(String id) {
        Agendamento agendamento = getAgendamentoPorId(id);
        if (agendamento == null || agendamento.getStatus() != StatusAgendamento.PENDENTE) {
            return false;
        }
        agendamento.setStatus(StatusAgendamento.CANCELADO);
        agendamentoRepository.save(agendamento);
        return true;
    }

    public List<String> getHorariosDisponiveis(LocalDate data) {
        List<String> configurados = adminService.getHorariosPorData(data);
        return configurados.stream()
                .filter(h -> {
                    try {
                        LocalTime horario = LocalTime.parse(h);
                        return !agendamentoRepository.existsByDataAndHorarioAndStatusIn(
                                data, horario, STATUS_ATIVOS);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public Page<Agendamento> getTodosAgendamentos(Pageable pageable) {
        return agendamentoRepository.findAllByOrderByDataDescHorarioDesc(pageable);
    }

    public List<Agendamento> getTodosAgendamentos() {
        return agendamentoRepository.findAll();
    }

    @Transactional
    public boolean removerAgendamento(String id) {
        if (agendamentoRepository.existsById(id)) {
            agendamentoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public long contarPorStatus(StatusAgendamento status) {
        return agendamentoRepository.countByStatus(status);
    }

    public List<Agendamento> getAgendamentosParaLembrete() {
        return agendamentoRepository.findByStatusInAndLembrete24hEnviadoFalseAndDataGreaterThanEqual(
                List.of(StatusAgendamento.CONFIRMADO), LocalDate.now());
    }

    @Transactional
    public void marcarLembrete24hEnviado(String id) {
        agendamentoRepository.findById(id).ifPresent(a -> {
            a.setLembrete24hEnviado(true);
            agendamentoRepository.save(a);
        });
    }

    @Transactional
    public void marcarLembrete1hEnviado(String id) {
        agendamentoRepository.findById(id).ifPresent(a -> {
            a.setLembrete1hEnviado(true);
            agendamentoRepository.save(a);
        });
    }
}
