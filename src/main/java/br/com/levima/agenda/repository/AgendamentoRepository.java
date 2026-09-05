package br.com.levima.agenda.repository;

import br.com.levima.agenda.model.Agendamento;
import br.com.levima.agenda.model.StatusAgendamento;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, String> {

    Page<Agendamento> findByEmailUsuarioOrderByDataDescHorarioDesc(String emailUsuario, Pageable pageable);

    List<Agendamento> findByEmailUsuarioOrderByDataDescHorarioDesc(String emailUsuario);

    Page<Agendamento> findAllByOrderByDataDescHorarioDesc(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM Agendamento a WHERE a.data = :data AND a.horario = :horario AND a.status IN :statuses")
    Optional<Agendamento> findAtivoComLock(@Param("data") LocalDate data,
                                           @Param("horario") LocalTime horario,
                                           @Param("statuses") List<StatusAgendamento> statuses);

    boolean existsByDataAndHorarioAndStatusIn(LocalDate data, LocalTime horario, List<StatusAgendamento> statuses);

    long countByEmailUsuarioAndDataBetweenAndStatusIn(String emailUsuario, LocalDate inicio, LocalDate fim,
                                                      List<StatusAgendamento> statuses);

    @Query("SELECT a FROM Agendamento a WHERE a.status IN :statuses AND a.data = :data")
    List<Agendamento> findByDataAndStatusIn(@Param("data") LocalDate data,
                                            @Param("statuses") List<StatusAgendamento> statuses);

    List<Agendamento> findByStatusInAndLembrete24hEnviadoFalseAndDataGreaterThanEqual(
            List<StatusAgendamento> statuses, LocalDate data);

    long countByStatus(StatusAgendamento status);
}
