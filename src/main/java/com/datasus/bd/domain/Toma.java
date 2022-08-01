package com.datasus.bd.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.time.LocalDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Toma.
 */
@Table("toma")
public class Toma implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("data")
    private LocalDate data;

    @Column("lote")
    private String lote;

    @Column("dose")
    private Long dose;

    @Transient
    private Vacina vacina;

    @Transient
    @JsonIgnoreProperties(value = { "condicoes" }, allowSetters = true)
    private Pessoa pessoa;

    @Column("vacina_id")
    private Long vacinaId;

    @Column("pessoa_id")
    private Long pessoaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Toma id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getData() {
        return this.data;
    }

    public Toma data(LocalDate data) {
        this.setData(data);
        return this;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public String getLote() {
        return this.lote;
    }

    public Toma lote(String lote) {
        this.setLote(lote);
        return this;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public Long getDose() {
        return this.dose;
    }

    public Toma dose(Long dose) {
        this.setDose(dose);
        return this;
    }

    public void setDose(Long dose) {
        this.dose = dose;
    }

    public Vacina getVacina() {
        return this.vacina;
    }

    public void setVacina(Vacina vacina) {
        this.vacina = vacina;
        this.vacinaId = vacina != null ? vacina.getId() : null;
    }

    public Toma vacina(Vacina vacina) {
        this.setVacina(vacina);
        return this;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
        this.pessoaId = pessoa != null ? pessoa.getId() : null;
    }

    public Toma pessoa(Pessoa pessoa) {
        this.setPessoa(pessoa);
        return this;
    }

    public Long getVacinaId() {
        return this.vacinaId;
    }

    public void setVacinaId(Long vacina) {
        this.vacinaId = vacina;
    }

    public Long getPessoaId() {
        return this.pessoaId;
    }

    public void setPessoaId(Long pessoa) {
        this.pessoaId = pessoa;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Toma)) {
            return false;
        }
        return id != null && id.equals(((Toma) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Toma{" +
            "id=" + getId() +
            ", data='" + getData() + "'" +
            ", lote='" + getLote() + "'" +
            ", dose=" + getDose() +
            "}";
    }
}
