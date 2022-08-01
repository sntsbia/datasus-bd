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
    @Column("id")
    private Long id;

    @Column("municipio_nome")
    private String municipioNome;

    @Transient
    private Uf uf;

    @Column("uf_id")
    private Long ufId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Municipio id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMunicipioNome() {
        return this.municipioNome;
    }

    public Municipio municipioNome(String municipioNome) {
        this.setMunicipioNome(municipioNome);
        return this;
    }

    public void setMunicipioNome(String municipioNome) {
        this.municipioNome = municipioNome;
    }

    public Uf getUf() {
        return this.uf;
    }

    public void setUf(Uf uf) {
        this.uf = uf;
        this.ufId = uf != null ? uf.getId() : null;
    }

    public Municipio uf(Uf uf) {
        this.setUf(uf);
        return this;
    }

    public Long getUfId() {
        return this.ufId;
    }

    public void setUfId(Long uf) {
        this.ufId = uf;
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
        return id != null && id.equals(((Municipio) o).id);
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
            "id=" + getId() +
            ", municipioNome='" + getMunicipioNome() + "'" +
            "}";
    }
}
