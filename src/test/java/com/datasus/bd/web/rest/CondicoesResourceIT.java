package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Condicoes;
import com.datasus.bd.repository.CondicoesRepository;
import com.datasus.bd.repository.EntityManager;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link CondicoesResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CondicoesResourceIT {

    private static final String DEFAULT_CONDICAO = "AAAAAAAAAA";
    private static final String UPDATED_CONDICAO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/condicoes";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CondicoesRepository condicoesRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Condicoes condicoes;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Condicoes createEntity(EntityManager em) {
        Condicoes condicoes = new Condicoes().condicao(DEFAULT_CONDICAO);
        return condicoes;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Condicoes createUpdatedEntity(EntityManager em) {
        Condicoes condicoes = new Condicoes().condicao(UPDATED_CONDICAO);
        return condicoes;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Condicoes.class).block();
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
        condicoes = createEntity(em);
    }

    @Test
    void createCondicoes() throws Exception {
        int databaseSizeBeforeCreate = condicoesRepository.findAll().collectList().block().size();
        // Create the Condicoes
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicoes))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeCreate + 1);
        Condicoes testCondicoes = condicoesList.get(condicoesList.size() - 1);
        assertThat(testCondicoes.getCondicao()).isEqualTo(DEFAULT_CONDICAO);
    }

    @Test
    void createCondicoesWithExistingId() throws Exception {
        // Create the Condicoes with an existing ID
        condicoes.setId(1L);

        int databaseSizeBeforeCreate = condicoesRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicoes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCondicoesAsStream() {
        // Initialize the database
        condicoesRepository.save(condicoes).block();

        List<Condicoes> condicoesList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Condicoes.class)
            .getResponseBody()
            .filter(condicoes::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(condicoesList).isNotNull();
        assertThat(condicoesList).hasSize(1);
        Condicoes testCondicoes = condicoesList.get(0);
        assertThat(testCondicoes.getCondicao()).isEqualTo(DEFAULT_CONDICAO);
    }

    @Test
    void getAllCondicoes() {
        // Initialize the database
        condicoesRepository.save(condicoes).block();

        // Get all the condicoesList
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
            .value(hasItem(condicoes.getId().intValue()))
            .jsonPath("$.[*].condicao")
            .value(hasItem(DEFAULT_CONDICAO));
    }

    @Test
    void getCondicoes() {
        // Initialize the database
        condicoesRepository.save(condicoes).block();

        // Get the condicoes
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, condicoes.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(condicoes.getId().intValue()))
            .jsonPath("$.condicao")
            .value(is(DEFAULT_CONDICAO));
    }

    @Test
    void getNonExistingCondicoes() {
        // Get the condicoes
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCondicoes() throws Exception {
        // Initialize the database
        condicoesRepository.save(condicoes).block();

        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();

        // Update the condicoes
        Condicoes updatedCondicoes = condicoesRepository.findById(condicoes.getId()).block();
        updatedCondicoes.condicao(UPDATED_CONDICAO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCondicoes.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCondicoes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
        Condicoes testCondicoes = condicoesList.get(condicoesList.size() - 1);
        assertThat(testCondicoes.getCondicao()).isEqualTo(UPDATED_CONDICAO);
    }

    @Test
    void putNonExistingCondicoes() throws Exception {
        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();
        condicoes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, condicoes.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicoes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCondicoes() throws Exception {
        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();
        condicoes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicoes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCondicoes() throws Exception {
        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();
        condicoes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicoes))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCondicoesWithPatch() throws Exception {
        // Initialize the database
        condicoesRepository.save(condicoes).block();

        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();

        // Update the condicoes using partial update
        Condicoes partialUpdatedCondicoes = new Condicoes();
        partialUpdatedCondicoes.setId(condicoes.getId());

        partialUpdatedCondicoes.condicao(UPDATED_CONDICAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCondicoes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCondicoes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
        Condicoes testCondicoes = condicoesList.get(condicoesList.size() - 1);
        assertThat(testCondicoes.getCondicao()).isEqualTo(UPDATED_CONDICAO);
    }

    @Test
    void fullUpdateCondicoesWithPatch() throws Exception {
        // Initialize the database
        condicoesRepository.save(condicoes).block();

        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();

        // Update the condicoes using partial update
        Condicoes partialUpdatedCondicoes = new Condicoes();
        partialUpdatedCondicoes.setId(condicoes.getId());

        partialUpdatedCondicoes.condicao(UPDATED_CONDICAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCondicoes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCondicoes))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
        Condicoes testCondicoes = condicoesList.get(condicoesList.size() - 1);
        assertThat(testCondicoes.getCondicao()).isEqualTo(UPDATED_CONDICAO);
    }

    @Test
    void patchNonExistingCondicoes() throws Exception {
        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();
        condicoes.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, condicoes.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicoes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCondicoes() throws Exception {
        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();
        condicoes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicoes))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCondicoes() throws Exception {
        int databaseSizeBeforeUpdate = condicoesRepository.findAll().collectList().block().size();
        condicoes.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicoes))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Condicoes in the database
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCondicoes() {
        // Initialize the database
        condicoesRepository.save(condicoes).block();

        int databaseSizeBeforeDelete = condicoesRepository.findAll().collectList().block().size();

        // Delete the condicoes
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, condicoes.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Condicoes> condicoesList = condicoesRepository.findAll().collectList().block();
        assertThat(condicoesList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
