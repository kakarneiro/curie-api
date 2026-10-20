package br.com.fiap.curie.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_CURIE_MEDICAO")
public class Medicao {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_curie_medicao")
    @SequenceGenerator(name = "sq_curie_medicao", sequenceName = "SQ_CURIE_MEDICAO", allocationSize = 1)
    @Column(name = "id_medicao")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_kit", nullable = false)
    private Kit kit;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    /** FK para T_CURIE_EXPERIMENTO. O catalogo de experimentos e gerenciado pela aplicacao .NET. */
    @Column(name = "id_experimento", nullable = false)
    private Long idExperimento;

    @Column(name = "valor_cpm", nullable = false, precision = 8, scale = 2)
    private BigDecimal valorCpm;

    @Column(name = "dt_hora", nullable = false)
    private LocalDateTime dtHora;

    @Column(name = "observacao", length = 200)
    private String observacao;

    @PrePersist
    void prePersist() {
        if (dtHora == null) dtHora = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Kit getKit() { return kit; }
    public void setKit(Kit kit) { this.kit = kit; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public Long getIdExperimento() { return idExperimento; }
    public void setIdExperimento(Long idExperimento) { this.idExperimento = idExperimento; }
    public BigDecimal getValorCpm() { return valorCpm; }
    public void setValorCpm(BigDecimal valorCpm) { this.valorCpm = valorCpm; }
    public LocalDateTime getDtHora() { return dtHora; }
    public void setDtHora(LocalDateTime dtHora) { this.dtHora = dtHora; }
    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}
