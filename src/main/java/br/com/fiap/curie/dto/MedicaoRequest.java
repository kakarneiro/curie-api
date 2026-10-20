package br.com.fiap.curie.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

@Schema(description = "Leitura do Geiger enviada pelo kit ou pelo app")
public record MedicaoRequest(
        @Schema(example = "1")
        @NotNull(message = "O id do kit e obrigatorio") @Positive Long idKit,

        @Schema(example = "1")
        @NotNull(message = "O id do usuario e obrigatorio") @Positive Long idUsuario,

        @Schema(example = "2", description = "Id do experimento no catalogo (T_CURIE_EXPERIMENTO)")
        @NotNull(message = "O id do experimento e obrigatorio") @Positive Long idExperimento,

        @Schema(example = "31.5", description = "Contagens por minuto")
        @NotNull(message = "O valor em CPM e obrigatorio")
        @DecimalMin(value = "0.0", message = "O valor em CPM nao pode ser negativo")
        @DecimalMax(value = "999999.99", message = "Valor em CPM acima do limite")
        BigDecimal valorCpm,

        @Schema(example = "Banana madura, sensor a 1 cm")
        @Size(max = 200, message = "A observacao deve ter no maximo 200 caracteres")
        String observacao
) {}
