package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Condicao;
import com.datasus.bd.repository.CondicaoRepository;
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
 * Integration tests for the {@link CondicaoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CondicaoResourceIT {

    private static final String DEFAULT_CONDICAO = "AAAAAAAAAA";
    private static final String UPDATED_CONDICAO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/condicaos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{idCondicao}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CondicaoRepository condicaoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Condicao condicao;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Condicao createEntity(EntityManager em) {
        Condicao condicao = new Condicao().condicao(DEFAULT_CONDICAO);
        return condicao;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Condicao createUpdatedEntity(EntityManager em) {
        Condicao condicao = new Condicao().condicao(UPDATED_CONDICAO);
        return condicao;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Condicao.class).block();
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
        condicao = createEntity(em);
    }

    @Test
    void createCondicao() throws Exception {
        int databaseSizeBeforeCreate = condicaoRepository.findAll().collectList().block().size();
        // Create the Condicao
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicao))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeCreate + 1);
        Condicao testCondicao = condicaoList.get(condicaoList.size() - 1);
        assertThat(testCondicao.getCondicao()).isEqualTo(DEFAULT_CONDICAO);
    }

    @Test
    void createCondicaoWithExistingId() throws Exception {
        // Create the Condicao with an existing ID
        condicao.setIdCondicao(1L);

        int databaseSizeBeforeCreate = condicaoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCondicaos() {
        // Initialize the database
        condicaoRepository.save(condicao).block();

        // Get all the condicaoList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=idCondicao,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].idCondicao")
            .value(hasItem(condicao.getIdCondicao().intValue()))
            .jsonPath("$.[*].condicao")
            .value(hasItem(DEFAULT_CONDICAO));
    }

    @Test
    void getCondicao() {
        // Initialize the database
        condicaoRepository.save(condicao).block();

        // Get the condicao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, condicao.getIdCondicao())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.idCondicao")
            .value(is(condicao.getIdCondicao().intValue()))
            .jsonPath("$.condicao")
            .value(is(DEFAULT_CONDICAO));
    }

    @Test
    void getNonExistingCondicao() {
        // Get the condicao
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCondicao() throws Exception {
        // Initialize the database
        condicaoRepository.save(condicao).block();

        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();

        // Update the condicao
        Condicao updatedCondicao = condicaoRepository.findById(condicao.getIdCondicao()).block();
        updatedCondicao.condicao(UPDATED_CONDICAO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCondicao.getIdCondicao())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCondicao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
        Condicao testCondicao = condicaoList.get(condicaoList.size() - 1);
        assertThat(testCondicao.getCondicao()).isEqualTo(UPDATED_CONDICAO);
    }

    @Test
    void putNonExistingCondicao() throws Exception {
        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();
        condicao.setIdCondicao(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, condicao.getIdCondicao())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCondicao() throws Exception {
        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();
        condicao.setIdCondicao(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCondicao() throws Exception {
        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();
        condicao.setIdCondicao(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCondicaoWithPatch() throws Exception {
        // Initialize the database
        condicaoRepository.save(condicao).block();

        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();

        // Update the condicao using partial update
        Condicao partialUpdatedCondicao = new Condicao();
        partialUpdatedCondicao.setIdCondicao(condicao.getIdCondicao());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCondicao.getIdCondicao())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCondicao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
        Condicao testCondicao = condicaoList.get(condicaoList.size() - 1);
        assertThat(testCondicao.getCondicao()).isEqualTo(DEFAULT_CONDICAO);
    }

    @Test
    void fullUpdateCondicaoWithPatch() throws Exception {
        // Initialize the database
        condicaoRepository.save(condicao).block();

        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();

        // Update the condicao using partial update
        Condicao partialUpdatedCondicao = new Condicao();
        partialUpdatedCondicao.setIdCondicao(condicao.getIdCondicao());

        partialUpdatedCondicao.condicao(UPDATED_CONDICAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCondicao.getIdCondicao())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCondicao))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
        Condicao testCondicao = condicaoList.get(condicaoList.size() - 1);
        assertThat(testCondicao.getCondicao()).isEqualTo(UPDATED_CONDICAO);
    }

    @Test
    void patchNonExistingCondicao() throws Exception {
        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();
        condicao.setIdCondicao(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, condicao.getIdCondicao())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCondicao() throws Exception {
        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();
        condicao.setIdCondicao(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicao))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCondicao() throws Exception {
        int databaseSizeBeforeUpdate = condicaoRepository.findAll().collectList().block().size();
        condicao.setIdCondicao(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(condicao))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Condicao in the database
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCondicao() {
        // Initialize the database
        condicaoRepository.save(condicao).block();

        int databaseSizeBeforeDelete = condicaoRepository.findAll().collectList().block().size();

        // Delete the condicao
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, condicao.getIdCondicao())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Condicao> condicaoList = condicaoRepository.findAll().collectList().block();
        assertThat(condicaoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
