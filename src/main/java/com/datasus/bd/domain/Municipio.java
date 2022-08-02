package com.datasus.bd.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * not an ignored comment
 */
@Schema(description = "not an ignored comment")
@Table("municipio")
public class Municipio implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id_municipio")
    private Long idMunicipio;

    @Column("municipio")
    private String municipio;

    @Transient
    private Uf fkIdUf;

    @Column("fk_id_uf_id_uf")
    private Long fkIdUfId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getIdMunicipio() {
        return this.idMunicipio;
    }

    public Municipio idMunicipio(Long idMunicipio) {
        this.setIdMunicipio(idMunicipio);
        return this;
    }

    public void setIdMunicipio(Long idMunicipio) {
        this.idMunicipio = idMunicipio;
    }

    public String getMunicipio() {
        return this.municipio;
    }

    public Municipio municipio(String municipio) {
        this.setMunicipio(municipio);
        return this;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public Uf getFkIdUf() {
        return this.fkIdUf;
    }

    public void setFkIdUf(Uf uf) {
        this.fkIdUf = uf;
        this.fkIdUfId = uf != null ? uf.getIdUf() : null;
    }

    public Municipio fkIdUf(Uf uf) {
        this.setFkIdUf(uf);
        return this;
    }

    public Long getFkIdUfId() {
        return this.fkIdUfId;
    }

    public void setFkIdUfId(Long uf) {
        this.fkIdUfId = uf;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Municipio)) {
            return false;
        }
        return idMunicipio != null && idMunicipio.equals(((Municipio) o).idMunicipio);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Municipio{" +
            "idMunicipio=" + getIdMunicipio() +
            ", municipio='" + getMunicipio() + "'" +
            "}";
    }
}
