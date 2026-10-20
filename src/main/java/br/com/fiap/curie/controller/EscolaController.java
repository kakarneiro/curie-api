package br.com.fiap.curie.controller;

import br.com.fiap.curie.dto.EscolaRequest;
import br.com.fiap.curie.dto.EscolaResponse;
import br.com.fiap.curie.exception.ApiError;
import br.com.fiap.curie.service.EscolaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/escolas")
@Tag(name = "Escolas")
public class EscolaController {

    private final EscolaService service;

    public EscolaController(EscolaService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar escolas", description = "Lista paginada. Use `nome` para buscar por texto (Query Method findByNomeContainingIgnoreCase).")
    @ApiResponse(responseCode = "200", description = "Pagina de escolas")
    public Page<EscolaResponse> listar(
            @Parameter(description = "Trecho do nome da escola", example = "Caetano") @RequestParam(required = false) String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @GetMapping("/cidade/{cidade}")
    @Operation(summary = "Listar escolas de uma cidade")
    public List<EscolaResponse> listarPorCidade(@PathVariable String cidade) {
        return service.listarPorCidade(cidade);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar escola por id")
    @ApiResponse(responseCode = "200", description = "Escola encontrada")
    @ApiResponse(responseCode = "404", description = "Escola nao encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public EscolaResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar escola")
    @ApiResponse(responseCode = "201", description = "Escola criada")
    @ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "Codigo INEP ja cadastrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<EscolaResponse> criar(@RequestBody @Valid EscolaRequest req) {
        EscolaResponse criada = service.criar(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar escola")
    @ApiResponse(responseCode = "200", description = "Escola atualizada")
    @ApiResponse(responseCode = "404", description = "Escola nao encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public EscolaResponse atualizar(@PathVariable Long id, @RequestBody @Valid EscolaRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir escola")
    @ApiResponse(responseCode = "204", description = "Escola excluida")
    @ApiResponse(responseCode = "404", description = "Escola nao encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Escola possui usuarios ou kits vinculados", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
