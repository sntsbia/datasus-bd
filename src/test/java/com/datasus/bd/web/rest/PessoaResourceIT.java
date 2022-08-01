package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Pessoa;
import com.datasus.bd.domain.enumeration.Sexo;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.PessoaRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link PessoaResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PessoaResourceIT {

    private static final Sexo DEFAULT_SEXO = Sexo.F;
    private static final Sexo UPDATED_SEXO = Sexo.M;

    private static final Long DEFAULT_IDADE = 1L;
    private static final Long UPDATED_IDADE = 2L;

    private static final String ENTITY_API_URL = "/api/pessoas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PessoaRepository pessoaRepository;

    @Mock
    private PessoaRepository pessoaRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pessoa pessoa;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pessoa createEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa().sexo(DEFAULT_SEXO).idade(DEFAULT_IDADE);
        return pessoa;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pessoa createUpdatedEntity(EntityManager em) {
        Pessoa pessoa = new Pessoa().sexo(UPDATED_SEXO).idade(UPDATED_IDADE);
        return pessoa;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_pessoa__condicoes").block();
            em.deleteAll(Pessoa.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        pessoa = createEntity(em);
    }

    @Test
    void createPessoa() throws Exception {
        int databaseSizeBeforeCreate = pessoaRepository.findAll().collectList().block().size();
        // Create the Pessoa
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoa))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate + 1);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getSexo()).isEqualTo(DEFAULT_SEXO);
        assertThat(testPessoa.getIdade()).isEqualTo(DEFAULT_IDADE);
    }

    @Test
    void createPessoaWithExistingId() throws Exception {
        // Create the Pessoa with an existing ID
        pessoa.setId(1L);

        int databaseSizeBeforeCreate = pessoaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPessoasAsStream() {
        // Initialize the database
        pessoaRepository.save(pessoa).block();

        List<Pessoa> pessoaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Pessoa.class)
            .getResponseBody()
            .filter(pessoa::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pessoaList).isNotNull();
        assertThat(pessoaList).hasSize(1);
        Pessoa testPessoa = pessoaList.get(0);
        assertThat(testPessoa.getSexo()).isEqualTo(DEFAULT_SEXO);
        assertThat(testPessoa.getIdade()).isEqualTo(DEFAULT_IDADE);
    }

    @Test
    void getAllPessoas() {
        // Initialize the database
        pessoaRepository.save(pessoa).block();

        // Get all the pessoaList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(pessoa.getId().intValue()))
            .jsonPath("$.[*].sexo")
            .value(hasItem(DEFAULT_SEXO.toString()))
            .jsonPath("$.[*].idade")
            .value(hasItem(DEFAULT_IDADE.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPessoasWithEagerRelationshipsIsEnabled() {
        when(pessoaRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(pessoaRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllPessoasWithEagerRelationshipsIsNotEnabled() {
        when(pessoaRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(pessoaRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getPessoa() {
        // Initialize the database
        pessoaRepository.save(pessoa).block();

        // Get the pessoa
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pessoa.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pessoa.getId().intValue()))
            .jsonPath("$.sexo")
            .value(is(DEFAULT_SEXO.toString()))
            .jsonPath("$.idade")
            .value(is(DEFAULT_IDADE.intValue()));
    }

    @Test
    void getNonExistingPessoa() {
        // Get the pessoa
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPessoa() throws Exception {
        // Initialize the database
        pessoaRepository.save(pessoa).block();

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();

        // Update the pessoa
        Pessoa updatedPessoa = pessoaRepository.findById(pessoa.getId()).block();
        updatedPessoa.sexo(UPDATED_SEXO).idade(UPDATED_IDADE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPessoa.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPessoa))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoa.getIdade()).isEqualTo(UPDATED_IDADE);
    }

    @Test
    void putNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();
        pessoa.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pessoa.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();
        pessoa.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();
        pessoa.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoa))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePessoaWithPatch() throws Exception {
        // Initialize the database
        pessoaRepository.save(pessoa).block();

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();

        // Update the pessoa using partial update
        Pessoa partialUpdatedPessoa = new Pessoa();
        partialUpdatedPessoa.setId(pessoa.getId());

        partialUpdatedPessoa.sexo(UPDATED_SEXO).idade(UPDATED_IDADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoa.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoa))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoa.getIdade()).isEqualTo(UPDATED_IDADE);
    }

    @Test
    void fullUpdatePessoaWithPatch() throws Exception {
        // Initialize the database
        pessoaRepository.save(pessoa).block();

        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();

        // Update the pessoa using partial update
        Pessoa partialUpdatedPessoa = new Pessoa();
        partialUpdatedPessoa.setId(pessoa.getId());

        partialUpdatedPessoa.sexo(UPDATED_SEXO).idade(UPDATED_IDADE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPessoa.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPessoa))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
        Pessoa testPessoa = pessoaList.get(pessoaList.size() - 1);
        assertThat(testPessoa.getSexo()).isEqualTo(UPDATED_SEXO);
        assertThat(testPessoa.getIdade()).isEqualTo(UPDATED_IDADE);
    }

    @Test
    void patchNonExistingPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();
        pessoa.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pessoa.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();
        pessoa.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoa))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPessoa() throws Exception {
        int databaseSizeBeforeUpdate = pessoaRepository.findAll().collectList().block().size();
        pessoa.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pessoa))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pessoa in the database
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePessoa() {
        // Initialize the database
        pessoaRepository.save(pessoa).block();

        int databaseSizeBeforeDelete = pessoaRepository.findAll().collectList().block().size();

        // Delete the pessoa
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pessoa.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Pessoa> pessoaList = pessoaRepository.findAll().collectList().block();
        assertThat(pessoaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
