package br.com.fiap.curie.dto;

import br.com.fiap.curie.model.Escola;
import br.com.fiap.curie.model.TipoEscola;

import java.time.LocalDate;

public record EscolaResponse(
        Long id, String nome, String cidade, String uf, TipoEscola tipo, String codigoInep, LocalDate dtCadastro) {

    public static EscolaResponse from(Escola e) {
        return new EscolaResponse(e.getId(), e.getNome(), e.getCidade(), e.getUf(), e.getTipo(),
                e.getCodigoInep(), e.getDtCadastro());
    }
}
