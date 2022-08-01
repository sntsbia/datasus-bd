package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CondicoesTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Condicoes.class);
        Condicoes condicoes1 = new Condicoes();
        condicoes1.setId(1L);
        Condicoes condicoes2 = new Condicoes();
        condicoes2.setId(condicoes1.getId());
        assertThat(condicoes1).isEqualTo(condicoes2);
        condicoes2.setId(2L);
        assertThat(condicoes1).isNotEqualTo(condicoes2);
        condicoes1.setId(null);
        assertThat(condicoes1).isNotEqualTo(condicoes2);
    }
}
