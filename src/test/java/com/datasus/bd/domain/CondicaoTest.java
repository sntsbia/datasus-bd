package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CondicaoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Condicao.class);
        Condicao condicao1 = new Condicao();
        condicao1.setIdCondicao(1L);
        Condicao condicao2 = new Condicao();
        condicao2.setIdCondicao(condicao1.getIdCondicao());
        assertThat(condicao1).isEqualTo(condicao2);
        condicao2.setIdCondicao(2L);
        assertThat(condicao1).isNotEqualTo(condicao2);
        condicao1.setIdCondicao(null);
        assertThat(condicao1).isNotEqualTo(condicao2);
    }
}
