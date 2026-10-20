package br.com.fiap.curie.repository;

import br.com.fiap.curie.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /** Query Method: busca por texto no nome. */
    Page<Usuario> findByNomeContainingIgnoreCase(String nome, Pageable pageable);

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    /** Query Method navegando pelo relacionamento: usuarios de uma escola. */
    List<Usuario> findByEscolaIdOrderByPontosDesc(Long idEscola);

    /** Ranking geral: top 10 por pontos. */
    List<Usuario> findTop10ByOrderByPontosDesc();
}
