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
 * A Condicoes.
 */
@Table("condicoes")
public class Condicoes implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("condicao")
    private String condicao;

    @Transient
    @JsonIgnoreProperties(value = { "condicoes" }, allowSetters = true)
    private Set<Pessoa> pessoas = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Condicoes id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCondicao() {
        return this.condicao;
    }

    public Condicoes condicao(String condicao) {
        this.setCondicao(condicao);
        return this;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public Set<Pessoa> getPessoas() {
        return this.pessoas;
    }

    public void setPessoas(Set<Pessoa> pessoas) {
        if (this.pessoas != null) {
            this.pessoas.forEach(i -> i.removeCondicoes(this));
        }
        if (pessoas != null) {
            pessoas.forEach(i -> i.addCondicoes(this));
        }
        this.pessoas = pessoas;
    }

    public Condicoes pessoas(Set<Pessoa> pessoas) {
        this.setPessoas(pessoas);
        return this;
    }

    public Condicoes addPessoa(Pessoa pessoa) {
        this.pessoas.add(pessoa);
        pessoa.getCondicoes().add(this);
        return this;
    }

    public Condicoes removePessoa(Pessoa pessoa) {
        this.pessoas.remove(pessoa);
        pessoa.getCondicoes().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Condicoes)) {
            return false;
        }
        return id != null && id.equals(((Condicoes) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Condicoes{" +
            "id=" + getId() +
            ", condicao='" + getCondicao() + "'" +
            "}";
    }
}
