package br.com.levima.agenda.service;

import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.repository.ContatoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class LembreteService {

    private static final Logger logger = LoggerFactory.getLogger(LembreteService.class);

    private final AgendamentoService agendamentoService;
    private final VerificacaoService verificacaoService;
    private final ContatoRepository contatoRepository;

    public LembreteService(AgendamentoService agendamentoService,
                           VerificacaoService verificacaoService,
                           ContatoRepository contatoRepository) {
        this.agendamentoService = agendamentoService;
        this.verificacaoService = verificacaoService;
        this.contatoRepository = contatoRepository;
    }

    @Scheduled(cron = "0 */15 * * * *")
    public void enviarLembretes() {
        List<Agendamento> agendamentos = agendamentoService.getAgendamentosParaLembrete();
        LocalDateTime agora = LocalDateTime.now();

        for (Agendamento agendamento : agendamentos) {
            LocalDateTime dataHoraAgendamento = LocalDateTime.of(agendamento.getData(), agendamento.getHorario());

            contatoRepository.findById(agendamento.getEmailUsuario()).ifPresent(contato -> {
                String celular = contato.getCelular() != null ? contato.getCelular().replaceAll("[^0-9]", "") : "";

                if (!agendamento.isLembrete24hEnviado()) {
                    LocalDateTime limite24h = dataHoraAgendamento.minusHours(24);
                    if (!agora.isBefore(limite24h) && agora.isBefore(dataHoraAgendamento)) {
                        String msg = "Lembrete MeAgendaAI: voce tem agendamento amanha/as " +
                                agendamento.getHorario() + " em " +
                                agendamento.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                        if (!celular.isEmpty()) {
                            verificacaoService.enviarMensagem(celular, msg);
                        }
                        agendamentoService.marcarLembrete24hEnviado(agendamento.getId());
                        logger.info("Lembrete 24h enviado para {}", agendamento.getEmailUsuario());
                    }
                }

                if (!agendamento.isLembrete1hEnviado()) {
                    LocalDateTime limite1h = dataHoraAgendamento.minusHours(1);
                    if (!agora.isBefore(limite1h) && agora.isBefore(dataHoraAgendamento)) {
                        String msg = "Lembrete MeAgendaAI: seu agendamento e em 1 hora (" +
                                agendamento.getHorario() + ").";
                        if (!celular.isEmpty()) {
                            verificacaoService.enviarMensagem(celular, msg);
                        }
                        agendamentoService.marcarLembrete1hEnviado(agendamento.getId());
                        logger.info("Lembrete 1h enviado para {}", agendamento.getEmailUsuario());
                    }
                }
            });
        }
    }
}
