package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class VacinaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Vacina.class);
        Vacina vacina1 = new Vacina();
        vacina1.setIdVacina(1L);
        Vacina vacina2 = new Vacina();
        vacina2.setIdVacina(vacina1.getIdVacina());
        assertThat(vacina1).isEqualTo(vacina2);
        vacina2.setIdVacina(2L);
        assertThat(vacina1).isNotEqualTo(vacina2);
        vacina1.setIdVacina(null);
        assertThat(vacina1).isNotEqualTo(vacina2);
    }
}
