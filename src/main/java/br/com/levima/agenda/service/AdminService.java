package br.com.levima.agenda.service;

import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminService {

    // Armazenar horários disponíveis por data
    private static Map<LocalDate, List<String>> horariosDisponiveisPorData = new HashMap<>();

    // Armazenar dias disponíveis selecionados
    private static Set<LocalDate> diasDisponiveis = new HashSet<>();

    // Armazenar login do admin
    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "meag_456";

    // Sessão do admin
    private static String adminLogado = null;

    public AdminService() {
        // Iniciar vazio - admin vai definindo as datas e horários
    }

    private void gerarHorariosDefault(LocalDate data) {
        List<String> horarios = new ArrayList<>();
        for (int i = 8; i < 17; i++) {
            horarios.add(String.format("%02d:00", i));
        }
        horariosDisponiveisPorData.put(data, horarios);
    }

    public boolean validarLogin(String usuario, String senha) {
        if (ADMIN_USER.equals(usuario) && ADMIN_PASSWORD.equals(senha)) {
            adminLogado = usuario;
            return true;
        }
        return false;
    }

    public boolean estaLogado() {
        return adminLogado != null;
    }

    public void logout() {
        adminLogado = null;
    }

    public Set<LocalDate> getDiasDisponiveis() {
        return new HashSet<>(diasDisponiveis);
    }

    public Map<LocalDate, List<String>> getHorariosDisponiveisPorData() {
        return new HashMap<>(horariosDisponiveisPorData);
    }

    public List<String> getHorariosPorData(LocalDate data) {
        return horariosDisponiveisPorData.getOrDefault(data, new ArrayList<>());
    }

    /**
     * Gera horários automaticamente baseado em período e intervalo
     * @param datas Lista de datas selecionadas
     * @param horaInicio Hora inicial (0-23)
     * @param horaFim Hora final (0-23)
     * @param intervaloMinutos Intervalo em minutos (30 ou 60)
     */
    public void gerarHorariosAutomaticos(List<String> datas, int horaInicio, int horaFim, int intervaloMinutos) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

            for (String dataStr : datas) {
                LocalDate data = LocalDate.parse(dataStr, formatter);
                List<String> horarios = new ArrayList<>();

                // Gerar horários no intervalo especificado
                for (int hora = horaInicio; hora < horaFim; hora++) {
                    if (intervaloMinutos == 30) {
                        horarios.add(String.format("%02d:00", hora));
                        horarios.add(String.format("%02d:30", hora));
                    } else {
                        horarios.add(String.format("%02d:00", hora));
                    }
                }

                // Adicionar o último horário se não foi adicionado
                if (intervaloMinutos == 30 && horaFim < 24) {
                    horarios.add(String.format("%02d:00", horaFim));
                }

                // Armazenar horários para a data
                horariosDisponiveisPorData.put(data, horarios);
                diasDisponiveis.add(data);
            }
        } catch (Exception e) {
            System.err.println("Erro ao gerar horários: " + e.getMessage());
        }
    }

    /**
     * Atualiza a seleção de dias disponíveis
     */
    public void atualizarDiasDisponiveis(List<String> datas) {
        diasDisponiveis.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        for (String dataStr : datas) {
            try {
                LocalDate data = LocalDate.parse(dataStr, formatter);
                diasDisponiveis.add(data);
                // Se não tiver horários para essa data, gerar padrão
                if (!horariosDisponiveisPorData.containsKey(data)) {
                    gerarHorariosDefault(data);
                }
            } catch (Exception e) {
                System.err.println("Erro ao adicionar data: " + e.getMessage());
            }
        }
    }

    /**
     * Remove uma data da disponibilidade
     */
    public void removerDataDisponivel(String data) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate dataObj = LocalDate.parse(data, formatter);
            diasDisponiveis.remove(dataObj);
            horariosDisponiveisPorData.remove(dataObj);
        } catch (Exception e) {
            System.err.println("Erro ao remover data: " + e.getMessage());
        }
    }

    /**
     * Remove um horário específico de uma data
     */
    public void removerHorario(String data, String horario) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            LocalDate dataObj = LocalDate.parse(data, formatter);
            if (horariosDisponiveisPorData.containsKey(dataObj)) {
                horariosDisponiveisPorData.get(dataObj).remove(horario);
            }
        } catch (Exception e) {
            System.err.println("Erro ao remover horário: " + e.getMessage());
        }
    }

    /**
     * Retorna a próxima data disponível a partir de hoje
     */
    public LocalDate getProximaDataDisponivel() {
        return diasDisponiveis.stream()
                .filter(d -> d.isAfter(LocalDate.now()))
                .min(LocalDate::compareTo)
                .orElse(null);
    }
}

