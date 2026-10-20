package br.com.fiap.curie.service;

import br.com.fiap.curie.dto.MedicaoRequest;
import br.com.fiap.curie.dto.MedicaoResponse;
import br.com.fiap.curie.exception.BusinessException;
import br.com.fiap.curie.exception.ResourceNotFoundException;
import br.com.fiap.curie.model.*;
import br.com.fiap.curie.repository.MedicaoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MedicaoService {

    /** Pontos que o usuario ganha por medicao registrada. */
    private static final int PONTOS_POR_MEDICAO = 10;

    private final MedicaoRepository repository;
    private final KitService kitService;
    private final UsuarioService usuarioService;

    public MedicaoService(MedicaoRepository repository, KitService kitService, UsuarioService usuarioService) {
        this.repository = repository;
        this.kitService = kitService;
        this.usuarioService = usuarioService;
    }

    @Transactional(readOnly = true)
    public Page<MedicaoResponse> listar(Pageable pageable) {
        return repository.findAll(pageable).map(MedicaoResponse::from);
    }

    @Transactional(readOnly = true)
    public MedicaoResponse buscar(Long id) {
        return MedicaoResponse.from(obter(id));
    }

    @Transactional(readOnly = true)
    public Page<MedicaoResponse> listarPorUsuario(Long idUsuario, Pageable pageable) {
        usuarioService.obter(idUsuario);
        return repository.findByUsuarioIdOrderByDtHoraDesc(idUsuario, pageable).map(MedicaoResponse::from);
    }

    @Transactional(readOnly = true)
    public List<MedicaoResponse> listarPorKit(Long idKit) {
        kitService.obter(idKit);
        return repository.findByKitIdOrderByDtHoraDesc(idKit).stream().map(MedicaoResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public List<MedicaoResponse> listarPorCidade(String cidade) {
        return repository.findByKitEscolaCidadeIgnoreCase(cidade).stream().map(MedicaoResponse::from).toList();
    }

    /**
     * Regras de negocio:
     *  - so kits ATIVOS registram medicao;
     *  - um ALUNO so mede com o kit da propria escola (professor pode operar kit visitante);
     *  - cada medicao registrada credita pontos ao usuario.
     */
    @Transactional
    public MedicaoResponse registrar(MedicaoRequest req) {
        Kit kit = kitService.obter(req.idKit());
        Usuario usuario = usuarioService.obter(req.idUsuario());

        if (kit.getStatus() != StatusKit.ATIVO) {
            throw new BusinessException("O kit " + kit.getCodSerial() + " nao esta ativo (status: " + kit.getStatus() + ")");
        }
        if (usuario.getPerfil() == Perfil.ALUNO && !kit.getEscola().getId().equals(usuario.getEscola().getId())) {
            throw new BusinessException("Alunos so podem registrar medicoes com o kit da propria escola");
        }

        Medicao m = new Medicao();
        m.setKit(kit);
        m.setUsuario(usuario);
        m.setIdExperimento(req.idExperimento());
        m.setValorCpm(req.valorCpm());
        m.setObservacao(req.observacao());

        usuario.adicionarPontos(PONTOS_POR_MEDICAO);
        return MedicaoResponse.from(repository.save(m));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    private Medicao obter(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Medicao", id));
    }
}
