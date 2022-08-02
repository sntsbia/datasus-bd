package com.datasus.bd.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Vacina.
 */
@Table("vacina")
public class Vacina implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id_vacina")
    private Long idVacina;

    @Column("fabricante")
    private String fabricante;

    @Column("nome_vacina")
    private String nomeVacina;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getIdVacina() {
        return this.idVacina;
    }

    public Vacina idVacina(Long idVacina) {
        this.setIdVacina(idVacina);
        return this;
    }

    public void setIdVacina(Long idVacina) {
        this.idVacina = idVacina;
    }

    public String getFabricante() {
        return this.fabricante;
    }

    public Vacina fabricante(String fabricante) {
        this.setFabricante(fabricante);
        return this;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getNomeVacina() {
        return this.nomeVacina;
    }

    public Vacina nomeVacina(String nomeVacina) {
        this.setNomeVacina(nomeVacina);
        return this;
    }

    public void setNomeVacina(String nomeVacina) {
        this.nomeVacina = nomeVacina;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Vacina)) {
            return false;
        }
        return idVacina != null && idVacina.equals(((Vacina) o).idVacina);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Vacina{" +
            "idVacina=" + getIdVacina() +
            ", fabricante='" + getFabricante() + "'" +
            ", nomeVacina='" + getNomeVacina() + "'" +
            "}";
    }
}
