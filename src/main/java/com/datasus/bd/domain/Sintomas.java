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
 * A Sintomas.
 */
@Table("sintomas")
public class Sintomas implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("descricao_sintoma")
    private String descricaoSintoma;

    @Transient
    @JsonIgnoreProperties(value = { "pessoa", "municipio", "sintomas" }, allowSetters = true)
    private Set<Teste> testes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Sintomas id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricaoSintoma() {
        return this.descricaoSintoma;
    }

    public Sintomas descricaoSintoma(String descricaoSintoma) {
        this.setDescricaoSintoma(descricaoSintoma);
        return this;
    }

    public void setDescricaoSintoma(String descricaoSintoma) {
        this.descricaoSintoma = descricaoSintoma;
    }

    public Set<Teste> getTestes() {
        return this.testes;
    }

    public void setTestes(Set<Teste> testes) {
        if (this.testes != null) {
            this.testes.forEach(i -> i.removeSintomas(this));
        }
        if (testes != null) {
            testes.forEach(i -> i.addSintomas(this));
        }
        this.testes = testes;
    }

    public Sintomas testes(Set<Teste> testes) {
        this.setTestes(testes);
        return this;
    }

    public Sintomas addTeste(Teste teste) {
        this.testes.add(teste);
        teste.getSintomas().add(this);
        return this;
    }

    public Sintomas removeTeste(Teste teste) {
        this.testes.remove(teste);
        teste.getSintomas().remove(this);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Sintomas)) {
            return false;
        }
        return id != null && id.equals(((Sintomas) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Sintomas{" +
            "id=" + getId() +
            ", descricaoSintoma='" + getDescricaoSintoma() + "'" +
            "}";
    }
}
