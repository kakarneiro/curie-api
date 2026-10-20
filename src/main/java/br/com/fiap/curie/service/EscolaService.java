package br.com.fiap.curie.service;

import br.com.fiap.curie.dto.EscolaRequest;
import br.com.fiap.curie.dto.EscolaResponse;
import br.com.fiap.curie.exception.BusinessException;
import br.com.fiap.curie.exception.ResourceNotFoundException;
import br.com.fiap.curie.model.Escola;
import br.com.fiap.curie.repository.EscolaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EscolaService {

    private final EscolaRepository repository;

    public EscolaService(EscolaRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public Page<EscolaResponse> listar(String nome, Pageable pageable) {
        Page<Escola> page = (nome == null || nome.isBlank())
                ? repository.findAll(pageable)
                : repository.findByNomeContainingIgnoreCase(nome.trim(), pageable);
        return page.map(EscolaResponse::from);
    }

    @Transactional(readOnly = true)
    public List<EscolaResponse> listarPorCidade(String cidade) {
        return repository.findByCidadeIgnoreCaseOrderByNome(cidade).stream().map(EscolaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public EscolaResponse buscar(Long id) {
        return EscolaResponse.from(obter(id));
    }

    @Transactional
    public EscolaResponse criar(EscolaRequest req) {
        if (req.codigoInep() != null && repository.existsByCodigoInep(req.codigoInep())) {
            throw new BusinessException("Ja existe uma escola com o codigo INEP " + req.codigoInep());
        }
        Escola e = new Escola();
        aplicar(e, req);
        return EscolaResponse.from(repository.save(e));
    }

    @Transactional
    public EscolaResponse atualizar(Long id, EscolaRequest req) {
        Escola e = obter(id);
        if (req.codigoInep() != null && !req.codigoInep().equals(e.getCodigoInep())
                && repository.existsByCodigoInep(req.codigoInep())) {
            throw new BusinessException("Ja existe uma escola com o codigo INEP " + req.codigoInep());
        }
        aplicar(e, req);
        return EscolaResponse.from(repository.save(e));
    }

    @Transactional
    public void excluir(Long id) {
        Escola e = obter(id);
        repository.delete(e);
    }

    Escola obter(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Escola", id));
    }

    private void aplicar(Escola e, EscolaRequest req) {
        e.setNome(req.nome().trim());
        e.setCidade(req.cidade().trim());
        e.setUf(req.uf().toUpperCase());
        e.setTipo(req.tipo());
        e.setCodigoInep(req.codigoInep());
    }
}
