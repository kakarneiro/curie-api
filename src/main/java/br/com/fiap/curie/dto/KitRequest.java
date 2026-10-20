package br.com.fiap.curie.dto;

import br.com.fiap.curie.model.StatusKit;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(description = "Dados para cadastro ou atualizacao de um kit CURIE")
public record KitRequest(
        @Schema(example = "1")
        @NotNull(message = "O id da escola e obrigatorio")
        @Positive(message = "O id da escola deve ser positivo")
        Long idEscola,

        @Schema(example = "CURIE-2026-0006", description = "Serial gravado no ESP32")
        @NotBlank(message = "O serial e obrigatorio")
        @Pattern(regexp = "^CURIE-\\d{4}-\\d{4}$", message = "Serial no formato CURIE-AAAA-NNNN")
        String codSerial,

        @Schema(example = "ATIVO", allowableValues = {"ATIVO", "INATIVO", "MANUTENCAO"})
        @NotNull(message = "O status e obrigatorio")
        StatusKit status,

        @Schema(example = "2026-10-20", description = "Obrigatoria quando status = ATIVO")
        LocalDate dtAtivacao
) {}
