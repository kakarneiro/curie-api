package br.com.fiap.curie.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Formato padrao de erro da CURIE API")
public record ApiError(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem,
        String path,
        List<CampoErro> campos
) {
    public record CampoErro(String campo, String mensagem) {}
}
