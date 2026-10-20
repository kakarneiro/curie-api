package br.com.fiap.curie.repository;

import br.com.fiap.curie.model.Medicao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicaoRepository extends JpaRepository<Medicao, Long> {

    Page<Medicao> findByUsuarioIdOrderByDtHoraDesc(Long idUsuario, Pageable pageable);

    List<Medicao> findByKitIdOrderByDtHoraDesc(Long idKit);

    List<Medicao> findByIdExperimentoOrderByDtHoraDesc(Long idExperimento);

    /** Query Method navegando dois niveis: medicoes de uma cidade (base do mapa colaborativo). */
    List<Medicao> findByKitEscolaCidadeIgnoreCase(String cidade);
}
