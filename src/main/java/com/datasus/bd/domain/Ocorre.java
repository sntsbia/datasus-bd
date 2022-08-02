package com.datasus.bd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Ocorre.
 */
@Table("ocorre")
public class Ocorre implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Transient
    @JsonIgnoreProperties(value = { "fkIdTeste", "sintomas" }, allowSetters = true)
    private Teste fkIdTeste;

    @Transient
    @JsonIgnoreProperties(value = { "fkIdUf" }, allowSetters = true)
    private Municipio fkIdMunicipio;

    @Column("fk_id_teste_id_teste")
    private Long fkIdTesteId;

    @Column("fk_id_municipio_id_municipio")
    private Long fkIdMunicipioId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Ocorre id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Teste getFkIdTeste() {
        return this.fkIdTeste;
    }

    public void setFkIdTeste(Teste teste) {
        this.fkIdTeste = teste;
        this.fkIdTesteId = teste != null ? teste.getIdTeste() : null;
    }

    public Ocorre fkIdTeste(Teste teste) {
        this.setFkIdTeste(teste);
        return this;
    }

    public Municipio getFkIdMunicipio() {
        return this.fkIdMunicipio;
    }

    public void setFkIdMunicipio(Municipio municipio) {
        this.fkIdMunicipio = municipio;
        this.fkIdMunicipioId = municipio != null ? municipio.getIdMunicipio() : null;
    }

    public Ocorre fkIdMunicipio(Municipio municipio) {
        this.setFkIdMunicipio(municipio);
        return this;
    }

    public Long getFkIdTesteId() {
        return this.fkIdTesteId;
    }

    public void setFkIdTesteId(Long teste) {
        this.fkIdTesteId = teste;
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
        if (!(o instanceof Ocorre)) {
            return false;
        }
        return id != null && id.equals(((Ocorre) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Ocorre{" +
            "id=" + getId() +
            "}";
    }
}
