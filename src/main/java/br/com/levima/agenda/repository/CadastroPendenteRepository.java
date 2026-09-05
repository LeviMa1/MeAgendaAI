package br.com.levima.agenda.repository;

import br.com.levima.agenda.model.CadastroPendente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.time.Instant;

public interface CadastroPendenteRepository extends JpaRepository<CadastroPendente, String> {

    @Modifying
    @Query("DELETE FROM CadastroPendente c WHERE c.timestampCodigo < :limite")
    int deleteExpirados(Instant limite);
}
