package com.datasus.bd.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.datasus.bd.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PessoaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pessoa.class);
        Pessoa pessoa1 = new Pessoa();
        pessoa1.setIdPessoa(1L);
        Pessoa pessoa2 = new Pessoa();
        pessoa2.setIdPessoa(pessoa1.getIdPessoa());
        assertThat(pessoa1).isEqualTo(pessoa2);
        pessoa2.setIdPessoa(2L);
        assertThat(pessoa1).isNotEqualTo(pessoa2);
        pessoa1.setIdPessoa(null);
        assertThat(pessoa1).isNotEqualTo(pessoa2);
    }
}
