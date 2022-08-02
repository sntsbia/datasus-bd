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
    @Column("id_sintomas")
    private Long idSintomas;

    @Column("sintomas")
    private String sintomas;

    @Transient
    @JsonIgnoreProperties(value = { "fkIdTeste", "sintomas" }, allowSetters = true)
    private Set<Teste> fkIdTestes = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getIdSintomas() {
        return this.idSintomas;
    }

    public Sintomas idSintomas(Long idSintomas) {
        this.setIdSintomas(idSintomas);
        return this;
    }

    public void setIdSintomas(Long idSintomas) {
        this.idSintomas = idSintomas;
    }

    public String getSintomas() {
        return this.sintomas;
    }

    public Sintomas sintomas(String sintomas) {
        this.setSintomas(sintomas);
        return this;
    }

    public void setSintomas(String sintomas) {
        this.sintomas = sintomas;
    }

    public Set<Teste> getFkIdTestes() {
        return this.fkIdTestes;
    }

    public void setFkIdTestes(Set<Teste> testes) {
        if (this.fkIdTestes != null) {
            this.fkIdTestes.forEach(i -> i.setSintomas(null));
        }
        if (testes != null) {
            testes.forEach(i -> i.setSintomas(this));
        }
        this.fkIdTestes = testes;
    }

    public Sintomas fkIdTestes(Set<Teste> testes) {
        this.setFkIdTestes(testes);
        return this;
    }

    public Sintomas addFkIdTeste(Teste teste) {
        this.fkIdTestes.add(teste);
        teste.setSintomas(this);
        return this;
    }

    public Sintomas removeFkIdTeste(Teste teste) {
        this.fkIdTestes.remove(teste);
        teste.setSintomas(null);
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
        return idSintomas != null && idSintomas.equals(((Sintomas) o).idSintomas);
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
            "idSintomas=" + getIdSintomas() +
            ", sintomas='" + getSintomas() + "'" +
            "}";
    }
}
