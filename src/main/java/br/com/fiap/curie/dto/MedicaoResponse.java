package br.com.fiap.curie.dto;

import br.com.fiap.curie.model.Medicao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MedicaoResponse(Long id, Long idKit, String codSerialKit, Long idUsuario, String nomeUsuario,
                              Long idExperimento, BigDecimal valorCpm, LocalDateTime dtHora, String observacao,
                              String cidade, String uf) {

    public static MedicaoResponse from(Medicao m) {
        return new MedicaoResponse(m.getId(), m.getKit().getId(), m.getKit().getCodSerial(),
                m.getUsuario().getId(), m.getUsuario().getNome(), m.getIdExperimento(), m.getValorCpm(),
                m.getDtHora(), m.getObservacao(), m.getKit().getEscola().getCidade(), m.getKit().getEscola().getUf());
    }
}
