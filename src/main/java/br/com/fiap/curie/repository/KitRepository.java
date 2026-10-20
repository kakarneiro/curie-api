package br.com.fiap.curie.repository;

import br.com.fiap.curie.model.Kit;
import br.com.fiap.curie.model.StatusKit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KitRepository extends JpaRepository<Kit, Long> {

    Optional<Kit> findByCodSerialIgnoreCase(String codSerial);

    boolean existsByCodSerialIgnoreCase(String codSerial);

    List<Kit> findByEscolaId(Long idEscola);

    List<Kit> findByStatus(StatusKit status);
}
