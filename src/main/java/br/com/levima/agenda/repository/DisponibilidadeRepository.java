package br.com.levima.agenda.repository;

import br.com.levima.agenda.model.DisponibilidadeAdmin;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.Optional;

public interface DisponibilidadeRepository extends JpaRepository<DisponibilidadeAdmin, Long> {
    Optional<DisponibilidadeAdmin> findByData(LocalDate data);
    void deleteByData(LocalDate data);
}

