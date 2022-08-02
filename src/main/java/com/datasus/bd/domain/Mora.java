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
    @JsonIgnoreProperties(value = { "condicao" }, allowSetters = true)
    private Pessoa fkIdPessoa;

    @Transient
    @JsonIgnoreProperties(value = { "fkIdUf" }, allowSetters = true)
    private Municipio fkIdMunicipio;

    @Column("fk_id_pessoa_id_pessoa")
    private Long fkIdPessoaId;

    @Column("fk_id_municipio_id_municipio")
    private Long fkIdMunicipioId;

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

    public Pessoa getFkIdPessoa() {
        return this.fkIdPessoa;
    }

    public void setFkIdPessoa(Pessoa pessoa) {
        this.fkIdPessoa = pessoa;
        this.fkIdPessoaId = pessoa != null ? pessoa.getIdPessoa() : null;
    }

    public Mora fkIdPessoa(Pessoa pessoa) {
        this.setFkIdPessoa(pessoa);
        return this;
    }

    public Municipio getFkIdMunicipio() {
        return this.fkIdMunicipio;
    }

    public void setFkIdMunicipio(Municipio municipio) {
        this.fkIdMunicipio = municipio;
        this.fkIdMunicipioId = municipio != null ? municipio.getIdMunicipio() : null;
    }

    public Mora fkIdMunicipio(Municipio municipio) {
        this.setFkIdMunicipio(municipio);
        return this;
    }

    public Long getFkIdPessoaId() {
        return this.fkIdPessoaId;
    }

    public void setFkIdPessoaId(Long pessoa) {
        this.fkIdPessoaId = pessoa;
    }

    public Long getFkIdMunicipioId() {
        return this.fkIdMunicipioId;
    }

    public void setFkIdMunicipioId(Long municipio) {
        this.fkIdMunicipioId = municipio;
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
