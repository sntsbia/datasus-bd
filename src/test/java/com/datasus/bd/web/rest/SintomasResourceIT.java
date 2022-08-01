package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Sintomas;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.SintomasRepository;
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
 * Integration tests for the {@link SintomasResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class SintomasResourceIT {

    private static final String DEFAULT_DESCRICAO_SINTOMA = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO_SINTOMA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/sintomas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private SintomasRepository sintomasRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Sintomas sintomas;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sintomas createEntity(EntityManager em) {
        Sintomas sintomas = new Sintomas().descricaoSintoma(DEFAULT_DESCRICAO_SINTOMA);
        return sintomas;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Sintomas createUpdatedEntity(EntityManager em) {
        Sintomas sintomas = new Sintomas().descricaoSintoma(UPDATED_DESCRICAO_SINTOMA);
        return sintomas;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Sintomas.class).block();
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
        sintomas = createEntity(em);
    }

    @Test
    void createSintomas() throws Exception {
        int databaseSizeBeforeCreate = sintomasRepository.findAll().collectList().block().size();
        // Create the Sintomas
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sintomas))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeCreate + 1);
        Sintomas testSintomas = sintomasList.get(sintomasList.size() - 1);
        assertThat(testSintomas.getDescricaoSintoma()).isEqualTo(DEFAULT_DESCRICAO_SINTOMA);
    }

    @Test
    void createSintomasWithExistingId() throws Exception {
        // Create the Sintomas with an existing ID
        sintomas.setId(1L);

        int databaseSizeBeforeCreate = sintomasRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sintomas))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllSintomasAsStream() {
        // Initialize the database
        sintomasRepository.save(sintomas).block();

        List<Sintomas> sintomasList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Sintomas.class)
            .getResponseBody()
            .filter(sintomas::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(sintomasList).isNotNull();
        assertThat(sintomasList).hasSize(1);
        Sintomas testSintomas = sintomasList.get(0);
        assertThat(testSintomas.getDescricaoSintoma()).isEqualTo(DEFAULT_DESCRICAO_SINTOMA);
    }

    @Test
    void getAllSintomas() {
        // Initialize the database
        sintomasRepository.save(sintomas).block();

        // Get all the sintomasList
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
            .value(hasItem(sintomas.getId().intValue()))
            .jsonPath("$.[*].descricaoSintoma")
            .value(hasItem(DEFAULT_DESCRICAO_SINTOMA));
    }

    @Test
    void getSintomas() {
        // Initialize the database
        sintomasRepository.save(sintomas).block();

        // Get the sintomas
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, sintomas.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(sintomas.getId().intValue()))
            .jsonPath("$.descricaoSintoma")
            .value(is(DEFAULT_DESCRICAO_SINTOMA));
    }

    @Test
    void getNonExistingSintomas() {
        // Get the sintomas
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewSintomas() throws Exception {
        // Initialize the database
        sintomasRepository.save(sintomas).block();

        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();

        // Update the sintomas
        Sintomas updatedSintomas = sintomasRepository.findById(sintomas.getId()).block();
        updatedSintomas.descricaoSintoma(UPDATED_DESCRICAO_SINTOMA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedSintomas.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedSintomas))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
        Sintomas testSintomas = sintomasList.get(sintomasList.size() - 1);
        assertThat(testSintomas.getDescricaoSintoma()).isEqualTo(UPDATED_DESCRICAO_SINTOMA);
    }

    @Test
    void putNonExistingSintomas() throws Exception {
        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();
        sintomas.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, sintomas.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sintomas))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchSintomas() throws Exception {
        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();
        sintomas.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sintomas))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamSintomas() throws Exception {
        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();
        sintomas.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(sintomas))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateSintomasWithPatch() throws Exception {
        // Initialize the database
        sintomasRepository.save(sintomas).block();

        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();

        // Update the sintomas using partial update
        Sintomas partialUpdatedSintomas = new Sintomas();
        partialUpdatedSintomas.setId(sintomas.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSintomas.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSintomas))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
        Sintomas testSintomas = sintomasList.get(sintomasList.size() - 1);
        assertThat(testSintomas.getDescricaoSintoma()).isEqualTo(DEFAULT_DESCRICAO_SINTOMA);
    }

    @Test
    void fullUpdateSintomasWithPatch() throws Exception {
        // Initialize the database
        sintomasRepository.save(sintomas).block();

        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();

        // Update the sintomas using partial update
        Sintomas partialUpdatedSintomas = new Sintomas();
        partialUpdatedSintomas.setId(sintomas.getId());

        partialUpdatedSintomas.descricaoSintoma(UPDATED_DESCRICAO_SINTOMA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedSintomas.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedSintomas))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
        Sintomas testSintomas = sintomasList.get(sintomasList.size() - 1);
        assertThat(testSintomas.getDescricaoSintoma()).isEqualTo(UPDATED_DESCRICAO_SINTOMA);
    }

    @Test
    void patchNonExistingSintomas() throws Exception {
        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();
        sintomas.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, sintomas.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sintomas))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchSintomas() throws Exception {
        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();
        sintomas.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sintomas))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamSintomas() throws Exception {
        int databaseSizeBeforeUpdate = sintomasRepository.findAll().collectList().block().size();
        sintomas.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(sintomas))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Sintomas in the database
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteSintomas() {
        // Initialize the database
        sintomasRepository.save(sintomas).block();

        int databaseSizeBeforeDelete = sintomasRepository.findAll().collectList().block().size();

        // Delete the sintomas
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, sintomas.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Sintomas> sintomasList = sintomasRepository.findAll().collectList().block();
        assertThat(sintomasList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
