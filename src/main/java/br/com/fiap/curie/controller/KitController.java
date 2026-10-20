package br.com.fiap.curie.controller;

import br.com.fiap.curie.dto.KitRequest;
import br.com.fiap.curie.dto.KitResponse;
import br.com.fiap.curie.exception.ApiError;
import br.com.fiap.curie.model.StatusKit;
import br.com.fiap.curie.service.KitService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/kits")
@Tag(name = "Kits")
public class KitController {

    private final KitService service;

    public KitController(KitService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar kits", description = "Filtra por status quando informado.")
    public List<KitResponse> listar(
            @Parameter(description = "ATIVO, INATIVO ou MANUTENCAO") @RequestParam(required = false) StatusKit status) {
        return service.listar(status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar kit por id")
    @ApiResponse(responseCode = "404", description = "Kit nao encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public KitResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/serial/{serial}")
    @Operation(summary = "Buscar kit pelo serial gravado no ESP32", description = "Usado pelo firmware do kit para se identificar.")
    public KitResponse buscarPorSerial(@PathVariable String serial) {
        return service.buscarPorSerial(serial);
    }

    @PostMapping
    @Operation(summary = "Cadastrar kit")
    @ApiResponse(responseCode = "201", description = "Kit criado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "Serial duplicado ou kit ATIVO sem data de ativacao", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<KitResponse> criar(@RequestBody @Valid KitRequest req) {
        KitResponse criado = service.criar(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar kit")
    public KitResponse atualizar(@PathVariable Long id, @RequestBody @Valid KitRequest req) {
        return service.atualizar(id, req);
    }

    @PatchMapping("/{id}/ativar")
    @Operation(summary = "Ativar kit", description = "Regra de negocio: muda o status para ATIVO e grava a data de ativacao.")
    @ApiResponse(responseCode = "422", description = "Kit ja ativo", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public KitResponse ativar(@PathVariable Long id) {
        return service.ativar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir kit")
    @ApiResponse(responseCode = "409", description = "Kit possui medicoes vinculadas", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
