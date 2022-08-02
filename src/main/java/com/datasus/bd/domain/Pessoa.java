package com.datasus.bd.domain;

import com.datasus.bd.domain.enumeration.Sexo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Pessoa.
 */
@Table("pessoa")
public class Pessoa implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id_pessoa")
    private Long idPessoa;

    @Column("sexo")
    private Sexo sexo;

    @Column("idade")
    private Long idade;

    @Transient
    @JsonIgnoreProperties(value = { "fkIdPessoas" }, allowSetters = true)
    private Condicao condicao;

    @Column("condicao_id_condicao")
    private Long condicaoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getIdPessoa() {
        return this.idPessoa;
    }

    public Pessoa idPessoa(Long idPessoa) {
        this.setIdPessoa(idPessoa);
        return this;
    }

    public void setIdPessoa(Long idPessoa) {
        this.idPessoa = idPessoa;
    }

    public Sexo getSexo() {
        return this.sexo;
    }

    public Pessoa sexo(Sexo sexo) {
        this.setSexo(sexo);
        return this;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public Long getIdade() {
        return this.idade;
    }

    public Pessoa idade(Long idade) {
        this.setIdade(idade);
        return this;
    }

    public void setIdade(Long idade) {
        this.idade = idade;
    }

    public Condicao getCondicao() {
        return this.condicao;
    }

    public void setCondicao(Condicao condicao) {
        this.condicao = condicao;
        this.condicaoId = condicao != null ? condicao.getIdCondicao() : null;
    }

    public Pessoa condicao(Condicao condicao) {
        this.setCondicao(condicao);
        return this;
    }

    public Long getCondicaoId() {
        return this.condicaoId;
    }

    public void setCondicaoId(Long condicao) {
        this.condicaoId = condicao;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pessoa)) {
            return false;
        }
        return idPessoa != null && idPessoa.equals(((Pessoa) o).idPessoa);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pessoa{" +
            "idPessoa=" + getIdPessoa() +
            ", sexo='" + getSexo() + "'" +
            ", idade=" + getIdade() +
            "}";
    }
}
