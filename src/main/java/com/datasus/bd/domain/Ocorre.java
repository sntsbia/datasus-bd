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
    @JsonIgnoreProperties(value = { "pessoa", "municipio", "sintomas" }, allowSetters = true)
    private Teste teste;

    @Transient
    @JsonIgnoreProperties(value = { "uf" }, allowSetters = true)
    private Municipio municipio;

    @Column("teste_id")
    private Long testeId;

    @Column("municipio_id")
    private Long municipioId;

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

    public Teste getTeste() {
        return this.teste;
    }

    public void setTeste(Teste teste) {
        this.teste = teste;
        this.testeId = teste != null ? teste.getId() : null;
    }

    public Ocorre teste(Teste teste) {
        this.setTeste(teste);
        return this;
    }

    public Municipio getMunicipio() {
        return this.municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
        this.municipioId = municipio != null ? municipio.getId() : null;
    }

    public Ocorre municipio(Municipio municipio) {
        this.setMunicipio(municipio);
        return this;
    }

    public Long getTesteId() {
        return this.testeId;
    }

    public void setTesteId(Long teste) {
        this.testeId = teste;
    }

    public Long getMunicipioId() {
        return this.municipioId;
    }

    public void setMunicipioId(Long municipio) {
        this.municipioId = municipio;
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
