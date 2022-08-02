package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Ocorre;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.OcorreRepository;
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
 * Integration tests for the {@link OcorreResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class OcorreResourceIT {

    private static final String ENTITY_API_URL = "/api/ocorres";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private OcorreRepository ocorreRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Ocorre ocorre;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ocorre createEntity(EntityManager em) {
        Ocorre ocorre = new Ocorre();
        return ocorre;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Ocorre createUpdatedEntity(EntityManager em) {
        Ocorre ocorre = new Ocorre();
        return ocorre;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Ocorre.class).block();
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
        ocorre = createEntity(em);
    }

    @Test
    void createOcorre() throws Exception {
        int databaseSizeBeforeCreate = ocorreRepository.findAll().collectList().block().size();
        // Create the Ocorre
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ocorre))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeCreate + 1);
        Ocorre testOcorre = ocorreList.get(ocorreList.size() - 1);
    }

    @Test
    void createOcorreWithExistingId() throws Exception {
        // Create the Ocorre with an existing ID
        ocorre.setId(1L);

        int databaseSizeBeforeCreate = ocorreRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ocorre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllOcorres() {
        // Initialize the database
        ocorreRepository.save(ocorre).block();

        // Get all the ocorreList
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
            .value(hasItem(ocorre.getId().intValue()));
    }

    @Test
    void getOcorre() {
        // Initialize the database
        ocorreRepository.save(ocorre).block();

        // Get the ocorre
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, ocorre.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(ocorre.getId().intValue()));
    }

    @Test
    void getNonExistingOcorre() {
        // Get the ocorre
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewOcorre() throws Exception {
        // Initialize the database
        ocorreRepository.save(ocorre).block();

        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();

        // Update the ocorre
        Ocorre updatedOcorre = ocorreRepository.findById(ocorre.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedOcorre.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedOcorre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
        Ocorre testOcorre = ocorreList.get(ocorreList.size() - 1);
    }

    @Test
    void putNonExistingOcorre() throws Exception {
        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();
        ocorre.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, ocorre.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ocorre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchOcorre() throws Exception {
        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();
        ocorre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ocorre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamOcorre() throws Exception {
        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();
        ocorre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(ocorre))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateOcorreWithPatch() throws Exception {
        // Initialize the database
        ocorreRepository.save(ocorre).block();

        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();

        // Update the ocorre using partial update
        Ocorre partialUpdatedOcorre = new Ocorre();
        partialUpdatedOcorre.setId(ocorre.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOcorre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOcorre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
        Ocorre testOcorre = ocorreList.get(ocorreList.size() - 1);
    }

    @Test
    void fullUpdateOcorreWithPatch() throws Exception {
        // Initialize the database
        ocorreRepository.save(ocorre).block();

        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();

        // Update the ocorre using partial update
        Ocorre partialUpdatedOcorre = new Ocorre();
        partialUpdatedOcorre.setId(ocorre.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedOcorre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedOcorre))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
        Ocorre testOcorre = ocorreList.get(ocorreList.size() - 1);
    }

    @Test
    void patchNonExistingOcorre() throws Exception {
        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();
        ocorre.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, ocorre.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ocorre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchOcorre() throws Exception {
        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();
        ocorre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ocorre))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamOcorre() throws Exception {
        int databaseSizeBeforeUpdate = ocorreRepository.findAll().collectList().block().size();
        ocorre.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(ocorre))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Ocorre in the database
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteOcorre() {
        // Initialize the database
        ocorreRepository.save(ocorre).block();

        int databaseSizeBeforeDelete = ocorreRepository.findAll().collectList().block().size();

        // Delete the ocorre
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, ocorre.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Ocorre> ocorreList = ocorreRepository.findAll().collectList().block();
        assertThat(ocorreList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
