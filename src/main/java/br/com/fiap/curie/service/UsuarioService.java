package br.com.fiap.curie.service;

import br.com.fiap.curie.dto.UsuarioRequest;
import br.com.fiap.curie.dto.UsuarioResponse;
import br.com.fiap.curie.exception.BusinessException;
import br.com.fiap.curie.exception.ResourceNotFoundException;
import br.com.fiap.curie.model.Usuario;
import br.com.fiap.curie.repository.UsuarioRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final EscolaService escolaService;

    public UsuarioService(UsuarioRepository repository, EscolaService escolaService) {
        this.repository = repository;
        this.escolaService = escolaService;
    }

    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listar(String nome, Pageable pageable) {
        Page<Usuario> page = (nome == null || nome.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        return page.map(UsuarioResponse::from);
    }

    @Transactional(readOnly = true)
    public UsuarioResponse buscar(Long id) {
        return UsuarioResponse.from(obter(id));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listarPorEscola(Long idEscola) {
        escolaService.obter(idEscola);
        return repository.findByEscolaIdOrderByPontosDesc(idEscola).stream().map(UsuarioResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> ranking() {
        return repository.findTop10ByOrderByPontosDesc().stream().map(UsuarioResponse::from).toList();
    }

    @Transactional
    public UsuarioResponse criar(UsuarioRequest req) {
        if (repository.existsByEmailIgnoreCase(req.email())) {
            throw new BusinessException("Ja existe um usuario com o e-mail " + req.email());
        }
        Usuario u = new Usuario();
        aplicar(u, req);
        return UsuarioResponse.from(repository.save(u));
    }

    @Transactional
    public UsuarioResponse atualizar(Long id, UsuarioRequest req) {
        Usuario u = obter(id);
        repository.findByEmailIgnoreCase(req.email()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new BusinessException("Ja existe um usuario com o e-mail " + req.email());
            }
        });
        aplicar(u, req);
        return UsuarioResponse.from(repository.save(u));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    Usuario obter(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private void aplicar(Usuario u, UsuarioRequest req) {
        u.setEscola(escolaService.obter(req.idEscola()));
        u.setNome(req.nome().trim());
        u.setEmail(req.email().trim().toLowerCase());
        u.setPerfil(req.perfil());
    }
}
