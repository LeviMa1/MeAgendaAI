package br.com.levima.agenda.repository;

import br.com.levima.agenda.model.Contato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ContatoRepository extends JpaRepository<Contato, String> {
    Optional<Contato> findByEmail(String email);
}

