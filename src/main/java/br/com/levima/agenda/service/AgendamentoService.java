package br.com.levima.agenda.service;

import br.com.levima.agenda.model.Agendamento;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AgendamentoService {

    private static List<Agendamento> agendamentos = new ArrayList<>();

    /**
     * Cria um novo agendamento
     */
    public Agendamento criarAgendamento(String emailUsuario, String nomeUsuario, LocalDate data,
                                        LocalTime horario, String descricao) {

        // Validar se o horário já está marcado
        boolean horarioOcupado = agendamentos.stream()
                .anyMatch(a -> a.getData().equals(data) &&
                             a.getHorario().equals(horario) &&
                             a.getStatus().equals("confirmado"));

        if (horarioOcupado) {
            return null; // Horário já marcado
        }

        Agendamento agendamento = new Agendamento(emailUsuario, nomeUsuario, data, horario, descricao);
        agendamentos.add(agendamento);
        return agendamento;
    }

    /**
     * Obtém todos os agendamentos de um usuário
     */
    public List<Agendamento> getAgendamentosUsuario(String emailUsuario) {
        return agendamentos.stream()
                .filter(a -> a.getEmailUsuario().equals(emailUsuario))
                .collect(Collectors.toList());
    }

    /**
     * Obtém um agendamento por ID
     */
    public Agendamento getAgendamentoPorId(String id) {
        return agendamentos.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Cancela um agendamento
     */
    public boolean cancelarAgendamento(String id) {
        Agendamento agendamento = getAgendamentoPorId(id);
        if (agendamento != null) {
            agendamento.setStatus("cancelado");
            return true;
        }
        return false;
    }

    /**
     * Obtém horários disponíveis para uma data
     */
    public List<String> getHorariosDisponiveis(LocalDate data) {
        List<String> horariosDisponiveis = new ArrayList<>();

        // Horários de atendimento: 08:00 às 17:00, intervalo de 1 hora
        for (int hora = 8; hora < 17; hora++) {
            LocalTime horario = LocalTime.of(hora, 0);

            // Verificar se horário não está ocupado
            boolean estaOcupado = agendamentos.stream()
                    .anyMatch(a -> a.getData().equals(data) &&
                                 a.getHorario().equals(horario) &&
                                 a.getStatus().equals("confirmado"));

            if (!estaOcupado) {
                horariosDisponiveis.add(horario.toString());
            }
        }

        return horariosDisponiveis;
    }

    /**
     * Obtém todos os agendamentos (admin)
     */
    public List<Agendamento> getTodosAgendamentos() {
        return new ArrayList<>(agendamentos);
    }

    /**
     * Remove um agendamento completamente
     */
    public boolean removerAgendamento(String id) {
        Agendamento agendamento = getAgendamentoPorId(id);
        if (agendamento != null) {
            agendamentos.remove(agendamento);
            return true;
        }
        return false;
    }
}

