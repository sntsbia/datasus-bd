package com.datasus.bd.domain;

import com.datasus.bd.domain.enumeration.Sexo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
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
    @Column("id")
    private Long id;

    @Column("sexo")
    private Sexo sexo;

    @Column("idade")
    private Long idade;

    @Transient
    @JsonIgnoreProperties(value = { "pessoas" }, allowSetters = true)
    private Set<Condicoes> condicoes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pessoa id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Set<Condicoes> getCondicoes() {
        return this.condicoes;
    }

    public void setCondicoes(Set<Condicoes> condicoes) {
        this.condicoes = condicoes;
    }

    public Pessoa condicoes(Set<Condicoes> condicoes) {
        this.setCondicoes(condicoes);
        return this;
    }

    public Pessoa addCondicoes(Condicoes condicoes) {
        this.condicoes.add(condicoes);
        condicoes.getPessoas().add(this);
        return this;
    }

    public Pessoa removeCondicoes(Condicoes condicoes) {
        this.condicoes.remove(condicoes);
        condicoes.getPessoas().remove(this);
        return this;
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
        return id != null && id.equals(((Pessoa) o).id);
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
            "id=" + getId() +
            ", sexo='" + getSexo() + "'" +
            ", idade=" + getIdade() +
            "}";
    }
}
