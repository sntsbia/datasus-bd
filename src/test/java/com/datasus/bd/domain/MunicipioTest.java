package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MunicipioTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Municipio.class);
        Municipio municipio1 = new Municipio();
        municipio1.setIdMunicipio(1L);
        Municipio municipio2 = new Municipio();
        municipio2.setIdMunicipio(municipio1.getIdMunicipio());
        assertThat(municipio1).isEqualTo(municipio2);
        municipio2.setIdMunicipio(2L);
        assertThat(municipio1).isNotEqualTo(municipio2);
        municipio1.setIdMunicipio(null);
        assertThat(municipio1).isNotEqualTo(municipio2);
    }
}
