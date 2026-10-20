package br.com.fiap.curie.dto;

import br.com.fiap.curie.model.Perfil;
import br.com.fiap.curie.model.Usuario;

import java.time.LocalDate;

public record UsuarioResponse(
        Long id, String nome, String email, Perfil perfil, Integer pontos,
        Long idEscola, String nomeEscola, LocalDate dtCadastro) {

    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getPerfil(), u.getPontos(),
                u.getEscola().getId(), u.getEscola().getNome(), u.getDtCadastro());
    }
}
