package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UfTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Uf.class);
        Uf uf1 = new Uf();
        uf1.setIdUf(1L);
        Uf uf2 = new Uf();
        uf2.setIdUf(uf1.getIdUf());
        assertThat(uf1).isEqualTo(uf2);
        uf2.setIdUf(2L);
        assertThat(uf1).isNotEqualTo(uf2);
        uf1.setIdUf(null);
        assertThat(uf1).isNotEqualTo(uf2);
    }
}
