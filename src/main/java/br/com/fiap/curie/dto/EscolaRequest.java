package br.com.fiap.curie.dto;

import br.com.fiap.curie.model.TipoEscola;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados para cadastro ou atualizacao de uma escola")
public record EscolaRequest(
        @Schema(example = "E.E. Prof. Caetano de Campos")
        @NotBlank(message = "O nome e obrigatorio")
        @Size(max = 120, message = "O nome deve ter no maximo 120 caracteres")
        String nome,

        @Schema(example = "Sao Paulo")
        @NotBlank(message = "A cidade e obrigatoria")
        @Size(max = 80, message = "A cidade deve ter no maximo 80 caracteres")
        String cidade,

        @Schema(example = "SP")
        @NotBlank(message = "A UF e obrigatoria")
        @Pattern(regexp = "^[A-Za-z]{2}$", message = "A UF deve ter exatamente 2 letras")
        String uf,

        @Schema(example = "PUBLICA", allowableValues = {"PUBLICA", "PRIVADA"})
        @NotNull(message = "O tipo e obrigatorio (PUBLICA ou PRIVADA)")
        TipoEscola tipo,

        @Schema(example = "35000001", description = "Codigo INEP com 8 digitos (opcional)")
        @Pattern(regexp = "^\\d{8}$", message = "O codigo INEP deve ter 8 digitos")
        String codigoInep
) {}
