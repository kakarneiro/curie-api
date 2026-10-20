package br.com.fiap.curie.dto;

import br.com.fiap.curie.model.Kit;
import br.com.fiap.curie.model.StatusKit;

import java.time.LocalDate;

public record KitResponse(Long id, String codSerial, StatusKit status, LocalDate dtAtivacao,
                          Long idEscola, String nomeEscola, String cidade) {

    public static KitResponse from(Kit k) {
        return new KitResponse(k.getId(), k.getCodSerial(), k.getStatus(), k.getDtAtivacao(),
                k.getEscola().getId(), k.getEscola().getNome(), k.getEscola().getCidade());
    }
}
