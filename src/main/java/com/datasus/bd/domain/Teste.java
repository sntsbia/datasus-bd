package com.datasus.bd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Teste.
 */
@Table("teste")
public class Teste implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id_teste")
    private Long idTeste;

    @Column("data_teste")
    private LocalDate dataTeste;

    @Column("resultado")
    private Long resultado;

    @Transient
    @JsonIgnoreProperties(value = { "condicao" }, allowSetters = true)
    private Pessoa fkIdTeste;

    @Transient
    @JsonIgnoreProperties(value = { "fkIdTestes" }, allowSetters = true)
    private Sintomas sintomas;

    @Column("fk_id_teste_id_pessoa")
    private Long fkIdTesteId;

    @Column("sintomas_id_sintomas")
    private Long sintomasId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getIdTeste() {
        return this.idTeste;
    }

    public Teste idTeste(Long idTeste) {
        this.setIdTeste(idTeste);
        return this;
    }

    public void setIdTeste(Long idTeste) {
        this.idTeste = idTeste;
    }

    public LocalDate getDataTeste() {
        return this.dataTeste;
    }

    public Teste dataTeste(LocalDate dataTeste) {
        this.setDataTeste(dataTeste);
        return this;
    }

    public void setDataTeste(LocalDate dataTeste) {
        this.dataTeste = dataTeste;
    }

    public Long getResultado() {
        return this.resultado;
    }

    public Teste resultado(Long resultado) {
        this.setResultado(resultado);
        return this;
    }

    public void setResultado(Long resultado) {
        this.resultado = resultado;
    }

    public Pessoa getFkIdTeste() {
        return this.fkIdTeste;
    }

    public void setFkIdTeste(Pessoa pessoa) {
        this.fkIdTeste = pessoa;
        this.fkIdTesteId = pessoa != null ? pessoa.getIdPessoa() : null;
    }

    public Teste fkIdTeste(Pessoa pessoa) {
        this.setFkIdTeste(pessoa);
        return this;
    }

    public Sintomas getSintomas() {
        return this.sintomas;
    }

    public void setSintomas(Sintomas sintomas) {
        this.sintomas = sintomas;
        this.sintomasId = sintomas != null ? sintomas.getIdSintomas() : null;
    }

    public Teste sintomas(Sintomas sintomas) {
        this.setSintomas(sintomas);
        return this;
    }

    public Long getFkIdTesteId() {
        return this.fkIdTesteId;
    }

    public void setFkIdTesteId(Long pessoa) {
        this.fkIdTesteId = pessoa;
    }

    public Long getSintomasId() {
        return this.sintomasId;
    }

    public void setSintomasId(Long sintomas) {
        this.sintomasId = sintomas;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Teste)) {
            return false;
        }
        return idTeste != null && idTeste.equals(((Teste) o).idTeste);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Teste{" +
            "idTeste=" + getIdTeste() +
            ", dataTeste='" + getDataTeste() + "'" +
            ", resultado=" + getResultado() +
            "}";
    }
}
