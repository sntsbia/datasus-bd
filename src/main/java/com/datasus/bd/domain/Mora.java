package com.datasus.bd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Mora.
 */
@Table("mora")
public class Mora implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    @JsonIgnoreProperties(value = { "condicoes" }, allowSetters = true)
    private Pessoa pessoa;

    @Transient
    @JsonIgnoreProperties(value = { "uf" }, allowSetters = true)
    private Municipio municipio;

    @Column("pessoa_id")
    private Long pessoaId;

    @Column("municipio_id")
    private Long municipioId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Mora id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
        this.pessoaId = pessoa != null ? pessoa.getId() : null;
    }

    public Mora pessoa(Pessoa pessoa) {
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

    public Mora municipio(Municipio municipio) {
        this.setMunicipio(municipio);
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
        if (!(o instanceof Mora)) {
            return false;
        }
        return id != null && id.equals(((Mora) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Mora{" +
            "id=" + getId() +
            "}";
    }
}
