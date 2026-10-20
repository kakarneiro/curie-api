package br.com.fiap.curie.controller;

import br.com.fiap.curie.dto.UsuarioRequest;
import br.com.fiap.curie.dto.UsuarioResponse;
import br.com.fiap.curie.exception.ApiError;
import br.com.fiap.curie.service.UsuarioService;
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
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Lista paginada. Use `nome` para buscar por texto (Query Method).")
    public Page<UsuarioResponse> listar(
            @Parameter(description = "Trecho do nome", example = "Ana") @RequestParam(required = false) String nome,
            @ParameterObject @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return service.listar(nome, pageable);
    }

    @GetMapping("/ranking")
    @Operation(summary = "Ranking: 10 usuarios com mais pontos")
    public List<UsuarioResponse> ranking() {
        return service.ranking();
    }

    @GetMapping("/escola/{idEscola}")
    @Operation(summary = "Listar usuarios de uma escola, ordenados por pontos")
    @ApiResponse(responseCode = "404", description = "Escola nao encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public List<UsuarioResponse> listarPorEscola(@PathVariable Long idEscola) {
        return service.listarPorEscola(idEscola);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar usuario por id")
    @ApiResponse(responseCode = "404", description = "Usuario nao encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public UsuarioResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    @Operation(summary = "Cadastrar usuario")
    @ApiResponse(responseCode = "201", description = "Usuario criado")
    @ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Escola nao encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "E-mail ja cadastrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<UsuarioResponse> criar(@RequestBody @Valid UsuarioRequest req) {
        UsuarioResponse criado = service.criar(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(uri).body(criado);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar usuario")
    @ApiResponse(responseCode = "404", description = "Usuario ou escola nao encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public UsuarioResponse atualizar(@PathVariable Long id, @RequestBody @Valid UsuarioRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir usuario")
    @ApiResponse(responseCode = "204", description = "Usuario excluido")
    @ApiResponse(responseCode = "409", description = "Usuario possui medicoes vinculadas", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
