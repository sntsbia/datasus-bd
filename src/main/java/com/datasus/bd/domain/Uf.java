package com.datasus.bd.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Uf.
 */
@Table("uf")
public class Uf implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id_uf")
    private Long idUf;

    @Column("codigo_ibge")
    private Long codigoIbge;

    @Column("estado")
    private String estado;

    @Column("bandeira")
    private byte[] bandeira;

    @Column("bandeira_content_type")
    private String bandeiraContentType;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getIdUf() {
        return this.idUf;
    }

    public Uf idUf(Long idUf) {
        this.setIdUf(idUf);
        return this;
    }

    public void setIdUf(Long idUf) {
        this.idUf = idUf;
    }

    public Long getCodigoIbge() {
        return this.codigoIbge;
    }

    public Uf codigoIbge(Long codigoIbge) {
        this.setCodigoIbge(codigoIbge);
        return this;
    }

    public void setCodigoIbge(Long codigoIbge) {
        this.codigoIbge = codigoIbge;
    }

    public String getEstado() {
        return this.estado;
    }

    public Uf estado(String estado) {
        this.setEstado(estado);
        return this;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public byte[] getBandeira() {
        return this.bandeira;
    }

    public Uf bandeira(byte[] bandeira) {
        this.setBandeira(bandeira);
        return this;
    }

    public void setBandeira(byte[] bandeira) {
        this.bandeira = bandeira;
    }

    public String getBandeiraContentType() {
        return this.bandeiraContentType;
    }

    public Uf bandeiraContentType(String bandeiraContentType) {
        this.bandeiraContentType = bandeiraContentType;
        return this;
    }

    public void setBandeiraContentType(String bandeiraContentType) {
        this.bandeiraContentType = bandeiraContentType;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Uf)) {
            return false;
        }
        return idUf != null && idUf.equals(((Uf) o).idUf);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Uf{" +
            "idUf=" + getIdUf() +
            ", codigoIbge=" + getCodigoIbge() +
            ", estado='" + getEstado() + "'" +
            ", bandeira='" + getBandeira() + "'" +
            ", bandeiraContentType='" + getBandeiraContentType() + "'" +
            "}";
    }
}
