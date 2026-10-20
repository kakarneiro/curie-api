package br.com.fiap.curie.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "T_CURIE_ESCOLA")
public class Escola {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_curie_escola")
    @SequenceGenerator(name = "sq_curie_escola", sequenceName = "SQ_CURIE_ESCOLA", allocationSize = 1)
    @Column(name = "id_escola")
    private Long id;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "cidade", nullable = false, length = 80)
    private String cidade;

    @Column(name = "uf", nullable = false, length = 2)
    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoEscola tipo;

    @Column(name = "codigo_inep", length = 8)
    private String codigoInep;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDate dtCadastro;

    @PrePersist
    void prePersist() {
        if (dtCadastro == null) dtCadastro = LocalDate.now();
        if (uf != null) uf = uf.toUpperCase();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }
    public String getUf() { return uf; }
    public void setUf(String uf) { this.uf = uf; }
    public TipoEscola getTipo() { return tipo; }
    public void setTipo(TipoEscola tipo) { this.tipo = tipo; }
    public String getCodigoInep() { return codigoInep; }
    public void setCodigoInep(String codigoInep) { this.codigoInep = codigoInep; }
    public LocalDate getDtCadastro() { return dtCadastro; }
    public void setDtCadastro(LocalDate dtCadastro) { this.dtCadastro = dtCadastro; }
}
