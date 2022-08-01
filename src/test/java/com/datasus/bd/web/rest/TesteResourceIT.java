package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Teste;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.TesteRepository;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link TesteResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TesteResourceIT {

    private static final LocalDate DEFAULT_DATA_TESTE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA_TESTE = LocalDate.now(ZoneId.systemDefault());

    private static final Long DEFAULT_RESULTADO = 1L;
    private static final Long UPDATED_RESULTADO = 2L;

    private static final String ENTITY_API_URL = "/api/testes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TesteRepository testeRepository;

    @Mock
    private TesteRepository testeRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Teste teste;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teste createEntity(EntityManager em) {
        Teste teste = new Teste().dataTeste(DEFAULT_DATA_TESTE).resultado(DEFAULT_RESULTADO);
        return teste;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Teste createUpdatedEntity(EntityManager em) {
        Teste teste = new Teste().dataTeste(UPDATED_DATA_TESTE).resultado(UPDATED_RESULTADO);
        return teste;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_teste__sintomas").block();
            em.deleteAll(Teste.class).block();
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
        teste = createEntity(em);
    }

    @Test
    void createTeste() throws Exception {
        int databaseSizeBeforeCreate = testeRepository.findAll().collectList().block().size();
        // Create the Teste
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(teste))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeCreate + 1);
        Teste testTeste = testeList.get(testeList.size() - 1);
        assertThat(testTeste.getDataTeste()).isEqualTo(DEFAULT_DATA_TESTE);
        assertThat(testTeste.getResultado()).isEqualTo(DEFAULT_RESULTADO);
    }

    @Test
    void createTesteWithExistingId() throws Exception {
        // Create the Teste with an existing ID
        teste.setId(1L);

        int databaseSizeBeforeCreate = testeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(teste))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTestesAsStream() {
        // Initialize the database
        testeRepository.save(teste).block();

        List<Teste> testeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Teste.class)
            .getResponseBody()
            .filter(teste::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(testeList).isNotNull();
        assertThat(testeList).hasSize(1);
        Teste testTeste = testeList.get(0);
        assertThat(testTeste.getDataTeste()).isEqualTo(DEFAULT_DATA_TESTE);
        assertThat(testTeste.getResultado()).isEqualTo(DEFAULT_RESULTADO);
    }

    @Test
    void getAllTestes() {
        // Initialize the database
        testeRepository.save(teste).block();

        // Get all the testeList
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
            .value(hasItem(teste.getId().intValue()))
            .jsonPath("$.[*].dataTeste")
            .value(hasItem(DEFAULT_DATA_TESTE.toString()))
            .jsonPath("$.[*].resultado")
            .value(hasItem(DEFAULT_RESULTADO.intValue()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTestesWithEagerRelationshipsIsEnabled() {
        when(testeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(testeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTestesWithEagerRelationshipsIsNotEnabled() {
        when(testeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(testeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTeste() {
        // Initialize the database
        testeRepository.save(teste).block();

        // Get the teste
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, teste.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(teste.getId().intValue()))
            .jsonPath("$.dataTeste")
            .value(is(DEFAULT_DATA_TESTE.toString()))
            .jsonPath("$.resultado")
            .value(is(DEFAULT_RESULTADO.intValue()));
    }

    @Test
    void getNonExistingTeste() {
        // Get the teste
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewTeste() throws Exception {
        // Initialize the database
        testeRepository.save(teste).block();

        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();

        // Update the teste
        Teste updatedTeste = testeRepository.findById(teste.getId()).block();
        updatedTeste.dataTeste(UPDATED_DATA_TESTE).resultado(UPDATED_RESULTADO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedTeste.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedTeste))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
        Teste testTeste = testeList.get(testeList.size() - 1);
        assertThat(testTeste.getDataTeste()).isEqualTo(UPDATED_DATA_TESTE);
        assertThat(testTeste.getResultado()).isEqualTo(UPDATED_RESULTADO);
    }

    @Test
    void putNonExistingTeste() throws Exception {
        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();
        teste.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teste.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(teste))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTeste() throws Exception {
        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();
        teste.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(teste))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTeste() throws Exception {
        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();
        teste.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(teste))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTesteWithPatch() throws Exception {
        // Initialize the database
        testeRepository.save(teste).block();

        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();

        // Update the teste using partial update
        Teste partialUpdatedTeste = new Teste();
        partialUpdatedTeste.setId(teste.getId());

        partialUpdatedTeste.dataTeste(UPDATED_DATA_TESTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeste.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTeste))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
        Teste testTeste = testeList.get(testeList.size() - 1);
        assertThat(testTeste.getDataTeste()).isEqualTo(UPDATED_DATA_TESTE);
        assertThat(testTeste.getResultado()).isEqualTo(DEFAULT_RESULTADO);
    }

    @Test
    void fullUpdateTesteWithPatch() throws Exception {
        // Initialize the database
        testeRepository.save(teste).block();

        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();

        // Update the teste using partial update
        Teste partialUpdatedTeste = new Teste();
        partialUpdatedTeste.setId(teste.getId());

        partialUpdatedTeste.dataTeste(UPDATED_DATA_TESTE).resultado(UPDATED_RESULTADO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeste.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedTeste))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
        Teste testTeste = testeList.get(testeList.size() - 1);
        assertThat(testTeste.getDataTeste()).isEqualTo(UPDATED_DATA_TESTE);
        assertThat(testTeste.getResultado()).isEqualTo(UPDATED_RESULTADO);
    }

    @Test
    void patchNonExistingTeste() throws Exception {
        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();
        teste.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, teste.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(teste))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTeste() throws Exception {
        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();
        teste.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(teste))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTeste() throws Exception {
        int databaseSizeBeforeUpdate = testeRepository.findAll().collectList().block().size();
        teste.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(teste))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Teste in the database
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTeste() {
        // Initialize the database
        testeRepository.save(teste).block();

        int databaseSizeBeforeDelete = testeRepository.findAll().collectList().block().size();

        // Delete the teste
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, teste.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Teste> testeList = testeRepository.findAll().collectList().block();
        assertThat(testeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
