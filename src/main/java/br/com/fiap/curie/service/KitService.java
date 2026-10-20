package br.com.fiap.curie.service;

import br.com.fiap.curie.dto.KitRequest;
import br.com.fiap.curie.dto.KitResponse;
import br.com.fiap.curie.exception.BusinessException;
import br.com.fiap.curie.exception.ResourceNotFoundException;
import br.com.fiap.curie.model.Kit;
import br.com.fiap.curie.model.StatusKit;
import br.com.fiap.curie.repository.KitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class KitService {

    private final KitRepository repository;
    private final EscolaService escolaService;

    public KitService(KitRepository repository, EscolaService escolaService) {
        this.repository = repository;
        this.escolaService = escolaService;
    }

    @Transactional(readOnly = true)
    public List<KitResponse> listar(StatusKit status) {
        List<Kit> kits = status == null ? repository.findAll() : repository.findByStatus(status);
        return kits.stream().map(KitResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public KitResponse buscar(Long id) {
        return KitResponse.from(obter(id));
    }

    @Transactional(readOnly = true)
    public KitResponse buscarPorSerial(String serial) {
        return repository.findByCodSerialIgnoreCase(serial).map(KitResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Kit com serial " + serial + " nao encontrado"));
    }

    @Transactional
    public KitResponse criar(KitRequest req) {
        if (repository.existsByCodSerialIgnoreCase(req.codSerial())) {
            throw new BusinessException("Ja existe um kit com o serial " + req.codSerial());
        }
        Kit k = new Kit();
        aplicar(k, req);
        return KitResponse.from(repository.save(k));
    }

    @Transactional
    public KitResponse atualizar(Long id, KitRequest req) {
        Kit k = obter(id);
        repository.findByCodSerialIgnoreCase(req.codSerial()).ifPresent(outro -> {
            if (!outro.getId().equals(id)) throw new BusinessException("Ja existe um kit com o serial " + req.codSerial());
        });
        aplicar(k, req);
        return KitResponse.from(repository.save(k));
    }

    /** Regra de negocio: ativar um kit registra a data de ativacao automaticamente. */
    @Transactional
    public KitResponse ativar(Long id) {
        Kit k = obter(id);
        if (k.getStatus() == StatusKit.ATIVO) throw new BusinessException("O kit ja esta ativo");
        k.setStatus(StatusKit.ATIVO);
        k.setDtAtivacao(LocalDate.now());
        return KitResponse.from(repository.save(k));
    }

    @Transactional
    public void excluir(Long id) {
        repository.delete(obter(id));
    }

    Kit obter(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Kit", id));
    }

    private void aplicar(Kit k, KitRequest req) {
        if (req.status() == StatusKit.ATIVO && req.dtAtivacao() == null) {
            throw new BusinessException("Um kit ATIVO precisa de data de ativacao");
        }
        k.setEscola(escolaService.obter(req.idEscola()));
        k.setCodSerial(req.codSerial().toUpperCase());
        k.setStatus(req.status());
        k.setDtAtivacao(req.dtAtivacao());
    }
}
