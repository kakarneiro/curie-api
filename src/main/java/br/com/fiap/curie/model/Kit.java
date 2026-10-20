package br.com.fiap.curie.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "T_CURIE_KIT")
public class Kit {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_curie_kit")
    @SequenceGenerator(name = "sq_curie_kit", sequenceName = "SQ_CURIE_KIT", allocationSize = 1)
    @Column(name = "id_kit")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_escola", nullable = false)
    private Escola escola;

    @Column(name = "cod_serial", nullable = false, length = 20, unique = true)
    private String codSerial;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private StatusKit status = StatusKit.INATIVO;

    @Column(name = "dt_ativacao")
    private LocalDate dtAtivacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Escola getEscola() { return escola; }
    public void setEscola(Escola escola) { this.escola = escola; }
    public String getCodSerial() { return codSerial; }
    public void setCodSerial(String codSerial) { this.codSerial = codSerial; }
    public StatusKit getStatus() { return status; }
    public void setStatus(StatusKit status) { this.status = status; }
    public LocalDate getDtAtivacao() { return dtAtivacao; }
    public void setDtAtivacao(LocalDate dtAtivacao) { this.dtAtivacao = dtAtivacao; }
}
