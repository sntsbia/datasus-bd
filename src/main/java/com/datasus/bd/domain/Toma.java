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
    private Vacina fkIdVacina;

    @Transient
    @JsonIgnoreProperties(value = { "condicao" }, allowSetters = true)
    private Pessoa fkIdPessoa;

    @Column("fk_id_vacina_id_vacina")
    private Long fkIdVacinaId;

    @Column("fk_id_pessoa_id_pessoa")
    private Long fkIdPessoaId;

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

    public Vacina getFkIdVacina() {
        return this.fkIdVacina;
    }

    public void setFkIdVacina(Vacina vacina) {
        this.fkIdVacina = vacina;
        this.fkIdVacinaId = vacina != null ? vacina.getIdVacina() : null;
    }

    public Toma fkIdVacina(Vacina vacina) {
        this.setFkIdVacina(vacina);
        return this;
    }

    public Pessoa getFkIdPessoa() {
        return this.fkIdPessoa;
    }

    public void setFkIdPessoa(Pessoa pessoa) {
        this.fkIdPessoa = pessoa;
        this.fkIdPessoaId = pessoa != null ? pessoa.getIdPessoa() : null;
    }

    public Toma fkIdPessoa(Pessoa pessoa) {
        this.setFkIdPessoa(pessoa);
        return this;
    }

    public Long getFkIdVacinaId() {
        return this.fkIdVacinaId;
    }

    public void setFkIdVacinaId(Long vacina) {
        this.fkIdVacinaId = vacina;
    }

    public Long getFkIdPessoaId() {
        return this.fkIdPessoaId;
    }

    public void setFkIdPessoaId(Long pessoa) {
        this.fkIdPessoaId = pessoa;
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
