package com.datasus.bd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Condicao.
 */
@Table("condicao")
public class Condicao implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id_condicao")
    private Long idCondicao;

    @Column("condicao")
    private String condicao;

    @Transient
    @JsonIgnoreProperties(value = { "condicao" }, allowSetters = true)
    private Set<Pessoa> fkIdPessoas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getIdCondicao() {
        return this.idCondicao;
    }

    public Condicao idCondicao(Long idCondicao) {
        this.setIdCondicao(idCondicao);
        return this;
    }

    public void setIdCondicao(Long idCondicao) {
        this.idCondicao = idCondicao;
    }

    public String getCondicao() {
        return this.condicao;
    }

    public Condicao condicao(String condicao) {
        this.setCondicao(condicao);
        return this;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public Set<Pessoa> getFkIdPessoas() {
        return this.fkIdPessoas;
    }

    public void setFkIdPessoas(Set<Pessoa> pessoas) {
        if (this.fkIdPessoas != null) {
            this.fkIdPessoas.forEach(i -> i.setCondicao(null));
        }
        if (pessoas != null) {
            pessoas.forEach(i -> i.setCondicao(this));
        }
        this.fkIdPessoas = pessoas;
    }

    public Condicao fkIdPessoas(Set<Pessoa> pessoas) {
        this.setFkIdPessoas(pessoas);
        return this;
    }

    public Condicao addFkIdPessoa(Pessoa pessoa) {
        this.fkIdPessoas.add(pessoa);
        pessoa.setCondicao(this);
        return this;
    }

    public Condicao removeFkIdPessoa(Pessoa pessoa) {
        this.fkIdPessoas.remove(pessoa);
        pessoa.setCondicao(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Condicao)) {
            return false;
        }
        return idCondicao != null && idCondicao.equals(((Condicao) o).idCondicao);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Condicao{" +
            "idCondicao=" + getIdCondicao() +
            ", condicao='" + getCondicao() + "'" +
            "}";
    }
}
