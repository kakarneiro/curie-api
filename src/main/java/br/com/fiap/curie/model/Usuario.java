package br.com.fiap.curie.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "T_CURIE_USUARIO")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sq_curie_usuario")
    @SequenceGenerator(name = "sq_curie_usuario", sequenceName = "SQ_CURIE_USUARIO", allocationSize = 1)
    @Column(name = "id_usuario")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_escola", nullable = false)
    private Escola escola;

    @Column(name = "nome", nullable = false, length = 120)
    private String nome;

    @Column(name = "email", nullable = false, length = 150, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil", nullable = false, length = 10)
    private Perfil perfil;

    @Column(name = "pontos", nullable = false)
    private Integer pontos = 0;

    @Column(name = "dt_cadastro", nullable = false, updatable = false)
    private LocalDate dtCadastro;

    @PrePersist
    void prePersist() {
        if (dtCadastro == null) dtCadastro = LocalDate.now();
        if (pontos == null) pontos = 0;
        if (email != null) email = email.toLowerCase();
    }

    public void adicionarPontos(int qtd) { this.pontos = (this.pontos == null ? 0 : this.pontos) + qtd; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Escola getEscola() { return escola; }
    public void setEscola(Escola escola) { this.escola = escola; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public Perfil getPerfil() { return perfil; }
    public void setPerfil(Perfil perfil) { this.perfil = perfil; }
    public Integer getPontos() { return pontos; }
    public void setPontos(Integer pontos) { this.pontos = pontos; }
    public LocalDate getDtCadastro() { return dtCadastro; }
    public void setDtCadastro(LocalDate dtCadastro) { this.dtCadastro = dtCadastro; }
}
