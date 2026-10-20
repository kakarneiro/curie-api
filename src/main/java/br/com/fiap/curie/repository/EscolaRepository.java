package br.com.fiap.curie.repository;

import br.com.fiap.curie.model.Escola;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscolaRepository extends JpaRepository<Escola, Long> {

    /** Query Method: busca por texto no nome, sem diferenciar maiusculas/minusculas. */
    Page<Escola> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    /** Query Method: escolas de uma cidade. */
    List<Escola> findByCidadeIgnoreCaseOrderByNome(String cidade);

    boolean existsByCodigoInep(String codigoInep);
}
