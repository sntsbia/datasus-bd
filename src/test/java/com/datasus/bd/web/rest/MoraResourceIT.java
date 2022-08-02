package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Mora;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.MoraRepository;
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
 * Integration tests for the {@link MoraResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MoraResourceIT {

    private static final String ENTITY_API_URL = "/api/moras";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MoraRepository moraRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Mora mora;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mora createEntity(EntityManager em) {
        Mora mora = new Mora();
        return mora;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Mora createUpdatedEntity(EntityManager em) {
        Mora mora = new Mora();
        return mora;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Mora.class).block();
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
        mora = createEntity(em);
    }

    @Test
    void createMora() throws Exception {
        int databaseSizeBeforeCreate = moraRepository.findAll().collectList().block().size();
        // Create the Mora
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mora))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeCreate + 1);
        Mora testMora = moraList.get(moraList.size() - 1);
    }

    @Test
    void createMoraWithExistingId() throws Exception {
        // Create the Mora with an existing ID
        mora.setId(1L);

        int databaseSizeBeforeCreate = moraRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mora))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllMoras() {
        // Initialize the database
        moraRepository.save(mora).block();

        // Get all the moraList
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
            .value(hasItem(mora.getId().intValue()));
    }

    @Test
    void getMora() {
        // Initialize the database
        moraRepository.save(mora).block();

        // Get the mora
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, mora.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(mora.getId().intValue()));
    }

    @Test
    void getNonExistingMora() {
        // Get the mora
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewMora() throws Exception {
        // Initialize the database
        moraRepository.save(mora).block();

        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();

        // Update the mora
        Mora updatedMora = moraRepository.findById(mora.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMora.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedMora))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
        Mora testMora = moraList.get(moraList.size() - 1);
    }

    @Test
    void putNonExistingMora() throws Exception {
        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();
        mora.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, mora.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mora))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMora() throws Exception {
        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();
        mora.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mora))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMora() throws Exception {
        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();
        mora.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(mora))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMoraWithPatch() throws Exception {
        // Initialize the database
        moraRepository.save(mora).block();

        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();

        // Update the mora using partial update
        Mora partialUpdatedMora = new Mora();
        partialUpdatedMora.setId(mora.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMora.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMora))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
        Mora testMora = moraList.get(moraList.size() - 1);
    }

    @Test
    void fullUpdateMoraWithPatch() throws Exception {
        // Initialize the database
        moraRepository.save(mora).block();

        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();

        // Update the mora using partial update
        Mora partialUpdatedMora = new Mora();
        partialUpdatedMora.setId(mora.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMora.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMora))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
        Mora testMora = moraList.get(moraList.size() - 1);
    }

    @Test
    void patchNonExistingMora() throws Exception {
        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();
        mora.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, mora.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(mora))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMora() throws Exception {
        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();
        mora.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(mora))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMora() throws Exception {
        int databaseSizeBeforeUpdate = moraRepository.findAll().collectList().block().size();
        mora.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(mora))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Mora in the database
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMora() {
        // Initialize the database
        moraRepository.save(mora).block();

        int databaseSizeBeforeDelete = moraRepository.findAll().collectList().block().size();

        // Delete the mora
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, mora.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Mora> moraList = moraRepository.findAll().collectList().block();
        assertThat(moraList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
