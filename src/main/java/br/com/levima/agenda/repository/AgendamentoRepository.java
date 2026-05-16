package br.com.levima.agenda.repository;

import br.com.levima.agenda.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AgendamentoRepository extends JpaRepository<Agendamento, String> {
    List<Agendamento> findByEmailUsuario(String emailUsuario);
    boolean existsByDataAndHorarioAndStatus(LocalDate data, LocalTime horario, String status);
}

