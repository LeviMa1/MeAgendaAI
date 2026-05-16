package br.com.levima.agenda.service;

import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.repository.AgendamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    @Autowired
    private AgendamentoRepository agendamentoRepository;

    @Autowired
    private AdminService adminService;

    /**
     * Cria um novo agendamento
     */
    public Agendamento criarAgendamento(String emailUsuario, String nomeUsuario, LocalDate data,
                                        LocalTime horario, String descricao) {

        // Validar se o horário já está marcado
        boolean horarioOcupado = agendamentoRepository
                .existsByDataAndHorarioAndStatus(data, horario, "confirmado");

        if (horarioOcupado) return null; // Horário já marcado

        Agendamento agendamento = new Agendamento(emailUsuario, nomeUsuario, data, horario, descricao);
        return agendamentoRepository.save(agendamento);
    }

    /**
     * Obtém todos os agendamentos de um usuário
     */
    public List<Agendamento> getAgendamentosUsuario(String emailUsuario) {
        return agendamentoRepository.findByEmailUsuario(emailUsuario);
    }

    /**
     * Obtém um agendamento por ID
     */
    public Agendamento getAgendamentoPorId(String id) {
        return agendamentoRepository.findById(id).orElse(null);
    }

    /**
     * Cancela um agendamento
     */
    public boolean cancelarAgendamento(String id) {
        Agendamento agendamento = getAgendamentoPorId(id);
        if (agendamento != null) {
            agendamento.setStatus("cancelado");
            agendamentoRepository.save(agendamento);
            return true;
        }
        return false;
    }

    /**
     * Retorna horários configurados pelo admin para a data, filtrando os já reservados.
     */
    public List<String> getHorariosDisponiveis(LocalDate data) {
        List<String> configurados = adminService.getHorariosPorData(data);
        return configurados.stream()
                .filter(h -> {
                    try {
                        LocalTime horario = LocalTime.parse(h);
                        return !agendamentoRepository.existsByDataAndHorarioAndStatus(data, horario, "confirmado");
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtém todos os agendamentos (admin)
     */
    public List<Agendamento> getTodosAgendamentos() {
        return agendamentoRepository.findAll();
    }

    /**
     * Remove um agendamento completamente
     */
    public boolean removerAgendamento(String id) {
        if (agendamentoRepository.existsById(id)) {
            agendamentoRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
