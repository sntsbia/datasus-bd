package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TomaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Toma.class);
        Toma toma1 = new Toma();
        toma1.setId(1L);
        Toma toma2 = new Toma();
        toma2.setId(toma1.getId());
        assertThat(toma1).isEqualTo(toma2);
        toma2.setId(2L);
        assertThat(toma1).isNotEqualTo(toma2);
        toma1.setId(null);
        assertThat(toma1).isNotEqualTo(toma2);
    }
}
