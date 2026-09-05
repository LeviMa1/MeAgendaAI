package br.com.levima.agenda.service;

import br.com.levima.agenda.model.DisponibilidadeAdmin;
import br.com.levima.agenda.repository.DisponibilidadeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AdminService {

    private final DisponibilidadeRepository disponibilidadeRepository;

    public AdminService(DisponibilidadeRepository disponibilidadeRepository) {
        this.disponibilidadeRepository = disponibilidadeRepository;
    }

    public Set<LocalDate> getDiasDisponiveis() {
        return disponibilidadeRepository.findAll()
                .stream()
                .map(DisponibilidadeAdmin::getData)
                .filter(d -> d.isAfter(LocalDate.now()))
                .collect(java.util.stream.Collectors.toCollection(TreeSet::new));
    }

    public Map<LocalDate, List<String>> getHorariosDisponiveisPorData() {
        Map<LocalDate, List<String>> map = new TreeMap<>();
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
                if (intervaloMinutos == 30 && horaFim < 24) {
                    horarios.add(String.format("%02d:00", horaFim));
                }

                DisponibilidadeAdmin disp = disponibilidadeRepository.findByData(data)
                        .orElse(new DisponibilidadeAdmin());
                disp.setData(data);
                disp.setHorariosList(horarios);
                disponibilidadeRepository.save(disp);

            } catch (Exception e) {
                throw new IllegalArgumentException("Erro ao gerar horarios para " + dataStr + ": " + e.getMessage());
            }
        }
    }

    @Transactional
    public void removerDataDisponivel(String dataStr) {
        LocalDate data = LocalDate.parse(dataStr, DateTimeFormatter.ISO_LOCAL_DATE);
        disponibilidadeRepository.deleteByData(data);
    }

    public LocalDate getProximaDataDisponivel() {
        return getDiasDisponiveis().stream()
                .min(LocalDate::compareTo)
                .orElse(null);
    }
}
