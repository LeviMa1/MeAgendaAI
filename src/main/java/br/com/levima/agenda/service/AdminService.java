package br.com.levima.agenda.service;

import br.com.levima.agenda.model.DisponibilidadeAdmin;
import br.com.levima.agenda.repository.DisponibilidadeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASSWORD = "meag_456";
    private static final String SESSION_KEY = "adminLogado";

    @Autowired
    private DisponibilidadeRepository disponibilidadeRepository;

    // ====== Autenticação ======

    public boolean validarLogin(String usuario, String senha, HttpSession session) {
        if (ADMIN_USER.equals(usuario) && ADMIN_PASSWORD.equals(senha)) {
            session.setAttribute(SESSION_KEY, true);
            return true;
        }
        return false;
    }

    public boolean estaLogado(HttpSession session) {
        return Boolean.TRUE.equals(session.getAttribute(SESSION_KEY));
    }

    public void logout(HttpSession session) {
        session.removeAttribute(SESSION_KEY);
    }

    // ====== Disponibilidade ======

    public Set<LocalDate> getDiasDisponiveis() {
        return disponibilidadeRepository.findAll()
                .stream()
                .map(DisponibilidadeAdmin::getData)
                .collect(Collectors.toSet());
    }

    public Map<LocalDate, List<String>> getHorariosDisponiveisPorData() {
        Map<LocalDate, List<String>> map = new HashMap<>();
        disponibilidadeRepository.findAll().forEach(d -> map.put(d.getData(), d.getHorariosList()));
        return map;
    }

    public List<String> getHorariosPorData(LocalDate data) {
        return disponibilidadeRepository.findByData(data)
                .map(DisponibilidadeAdmin::getHorariosList)
                .orElse(Collections.emptyList());
    }

    @Transactional
    public void gerarHorariosAutomaticos(List<String> datas, int horaInicio, int horaFim, int intervaloMinutos) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        for (String dataStr : datas) {
            try {
                LocalDate data = LocalDate.parse(dataStr, formatter);
                List<String> horarios = new ArrayList<>();

                for (int hora = horaInicio; hora < horaFim; hora++) {
                    if (intervaloMinutos == 30) {
                        horarios.add(String.format("%02d:00", hora));
                        horarios.add(String.format("%02d:30", hora));
                    } else {
                        horarios.add(String.format("%02d:00", hora));
                    }
                }
                // Adiciona o horário final quando intervalo de 30 min
                if (intervaloMinutos == 30 && horaFim < 24) {
                    horarios.add(String.format("%02d:00", horaFim));
                }

                DisponibilidadeAdmin disp = disponibilidadeRepository.findByData(data)
                        .orElse(new DisponibilidadeAdmin());
                disp.setData(data);
                disp.setHorariosList(horarios);
                disponibilidadeRepository.save(disp);

            } catch (Exception e) {
                System.err.println("Erro ao gerar horários para " + dataStr + ": " + e.getMessage());
            }
        }
    }

    @Transactional
    public void removerDataDisponivel(String dataStr) {
        try {
            LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ISO_LOCAL_DATE);
            disponibilidadeRepository.deleteByData(data);
        } catch (Exception e) {
            System.err.println("Erro ao remover data: " + e.getMessage());
        }
    }

    public LocalDate getProximaDataDisponivel() {
        return getDiasDisponiveis().stream()
                .filter(d -> d.isAfter(LocalDate.now()))
                .min(LocalDate::compareTo)
                .orElse(null);
    }
}
