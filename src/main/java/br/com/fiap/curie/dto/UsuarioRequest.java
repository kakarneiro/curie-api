package br.com.fiap.curie.dto;

import br.com.fiap.curie.model.Perfil;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Dados para cadastro ou atualizacao de um usuario (aluno ou professor)")
public record UsuarioRequest(
        @Schema(example = "1")
        @NotNull(message = "O id da escola e obrigatorio")
        @Positive(message = "O id da escola deve ser positivo")
        Long idEscola,

        @Schema(example = "Ana Beatriz Souza")
        @NotBlank(message = "O nome e obrigatorio")
        @Size(min = 3, max = 120, message = "O nome deve ter entre 3 e 120 caracteres")
        String nome,

        @Schema(example = "ana.souza@aluno.curie.edu.br")
        @NotBlank(message = "O e-mail e obrigatorio")
        @Email(message = "E-mail invalido")
        @Size(max = 150)
        String email,

        @Schema(example = "ALUNO", allowableValues = {"ALUNO", "PROFESSOR"})
        @NotNull(message = "O perfil e obrigatorio (ALUNO ou PROFESSOR)")
        Perfil perfil
) {}
