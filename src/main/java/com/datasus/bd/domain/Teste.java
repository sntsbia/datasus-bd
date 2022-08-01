package com.datasus.bd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Teste.
 */
@Table("teste")
public class Teste implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("data_teste")
    private LocalDate dataTeste;

    @Column("resultado")
    private Long resultado;

    @Transient
    @JsonIgnoreProperties(value = { "condicoes" }, allowSetters = true)
    private Pessoa pessoa;

    @Transient
    @JsonIgnoreProperties(value = { "uf" }, allowSetters = true)
    private Municipio municipio;

    @Transient
    @JsonIgnoreProperties(value = { "testes" }, allowSetters = true)
    private Set<Sintomas> sintomas = new HashSet<>();

    @Column("pessoa_id")
    private Long pessoaId;

    @Column("municipio_id")
    private Long municipioId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Teste id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataTeste() {
        return this.dataTeste;
    }

    public Teste dataTeste(LocalDate dataTeste) {
        this.setDataTeste(dataTeste);
        return this;
    }

    public void setDataTeste(LocalDate dataTeste) {
        this.dataTeste = dataTeste;
    }

    public Long getResultado() {
        return this.resultado;
    }

    public Teste resultado(Long resultado) {
        this.setResultado(resultado);
        return this;
    }

    public void setResultado(Long resultado) {
        this.resultado = resultado;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
        this.pessoaId = pessoa != null ? pessoa.getId() : null;
    }

    public Teste pessoa(Pessoa pessoa) {
        this.setPessoa(pessoa);
        return this;
    }

    public Municipio getMunicipio() {
        return this.municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
        this.municipioId = municipio != null ? municipio.getId() : null;
    }

    public Teste municipio(Municipio municipio) {
        this.setMunicipio(municipio);
        return this;
    }

    public Set<Sintomas> getSintomas() {
        return this.sintomas;
    }

    public void setSintomas(Set<Sintomas> sintomas) {
        this.sintomas = sintomas;
    }

    public Teste sintomas(Set<Sintomas> sintomas) {
        this.setSintomas(sintomas);
        return this;
    }

    public Teste addSintomas(Sintomas sintomas) {
        this.sintomas.add(sintomas);
        sintomas.getTestes().add(this);
        return this;
    }

    public Teste removeSintomas(Sintomas sintomas) {
        this.sintomas.remove(sintomas);
        sintomas.getTestes().remove(this);
        return this;
    }

    public Long getPessoaId() {
        return this.pessoaId;
    }

    public void setPessoaId(Long pessoa) {
        this.pessoaId = pessoa;
    }

    public Long getMunicipioId() {
        return this.municipioId;
    }

    public void setMunicipioId(Long municipio) {
        this.municipioId = municipio;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Teste)) {
            return false;
        }
        return id != null && id.equals(((Teste) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Teste{" +
            "id=" + getId() +
            ", dataTeste='" + getDataTeste() + "'" +
            ", resultado=" + getResultado() +
            "}";
    }
}
