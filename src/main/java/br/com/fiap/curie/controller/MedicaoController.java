package br.com.fiap.curie.controller;

import br.com.fiap.curie.dto.MedicaoRequest;
import br.com.fiap.curie.dto.MedicaoResponse;
import br.com.fiap.curie.exception.ApiError;
import br.com.fiap.curie.service.MedicaoService;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/api/medicoes")
@Tag(name = "Medicoes")
public class MedicaoController {

    private final MedicaoService service;

    public MedicaoController(MedicaoService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Listar medicoes (paginado, mais recentes primeiro)")
    public Page<MedicaoResponse> listar(
            @ParameterObject @PageableDefault(size = 20, sort = "dtHora", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar medicao por id")
    @ApiResponse(responseCode = "404", description = "Medicao nao encontrada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public MedicaoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @GetMapping("/usuario/{idUsuario}")
    @Operation(summary = "Medicoes de um usuario", description = "Base da tela 'Minhas missoes' do app.")
    public Page<MedicaoResponse> porUsuario(@PathVariable Long idUsuario,
            @ParameterObject @PageableDefault(size = 20) Pageable pageable) {
        return service.listarPorUsuario(idUsuario, pageable);
    }

    @GetMapping("/kit/{idKit}")
    @Operation(summary = "Medicoes registradas por um kit")
    public List<MedicaoResponse> porKit(@PathVariable Long idKit) {
        return service.listarPorKit(idKit);
    }

    @GetMapping("/cidade/{cidade}")
    @Operation(summary = "Medicoes de uma cidade", description = "Base do mapa colaborativo de radiacao de fundo.")
    public List<MedicaoResponse> porCidade(@PathVariable String cidade) {
        return service.listarPorCidade(cidade);
    }

    @PostMapping
    @Operation(summary = "Registrar medicao",
            description = "Regras: o kit deve estar ATIVO; alunos so medem com o kit da propria escola; cada medicao credita 10 pontos ao usuario.")
    @ApiResponse(responseCode = "201", description = "Medicao registrada")
    @ApiResponse(responseCode = "400", description = "Dados invalidos", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "404", description = "Kit ou usuario nao encontrado", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "409", description = "Experimento inexistente", content = @Content(schema = @Schema(implementation = ApiError.class)))
    @ApiResponse(responseCode = "422", description = "Regra de negocio violada", content = @Content(schema = @Schema(implementation = ApiError.class)))
    public ResponseEntity<MedicaoResponse> registrar(@RequestBody @Valid MedicaoRequest req) {
        MedicaoResponse criada = service.registrar(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(criada.id()).toUri();
        return ResponseEntity.created(uri).body(criada);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Excluir medicao")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
