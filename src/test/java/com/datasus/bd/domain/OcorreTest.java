package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OcorreTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Ocorre.class);
        Ocorre ocorre1 = new Ocorre();
        ocorre1.setId(1L);
        Ocorre ocorre2 = new Ocorre();
        ocorre2.setId(ocorre1.getId());
        assertThat(ocorre1).isEqualTo(ocorre2);
        ocorre2.setId(2L);
        assertThat(ocorre1).isNotEqualTo(ocorre2);
        ocorre1.setId(null);
        assertThat(ocorre1).isNotEqualTo(ocorre2);
    }
}
