package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class MoraTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Mora.class);
        Mora mora1 = new Mora();
        mora1.setId(1L);
        Mora mora2 = new Mora();
        mora2.setId(mora1.getId());
        assertThat(mora1).isEqualTo(mora2);
        mora2.setId(2L);
        assertThat(mora1).isNotEqualTo(mora2);
        mora1.setId(null);
        assertThat(mora1).isNotEqualTo(mora2);
    }
}
