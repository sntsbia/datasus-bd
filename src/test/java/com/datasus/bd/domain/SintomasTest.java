package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SintomasTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Sintomas.class);
        Sintomas sintomas1 = new Sintomas();
        sintomas1.setIdSintomas(1L);
        Sintomas sintomas2 = new Sintomas();
        sintomas2.setIdSintomas(sintomas1.getIdSintomas());
        assertThat(sintomas1).isEqualTo(sintomas2);
        sintomas2.setIdSintomas(2L);
        assertThat(sintomas1).isNotEqualTo(sintomas2);
        sintomas1.setIdSintomas(null);
        assertThat(sintomas1).isNotEqualTo(sintomas2);
    }
}
