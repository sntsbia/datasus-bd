package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Toma;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.TomaRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link TomaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TomaResourceIT {

    private static final LocalDate DEFAULT_DATA = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATA = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_LOTE = "AAAAAAAAAA";
    private static final String UPDATED_LOTE = "BBBBBBBBBB";

    private static final Long DEFAULT_DOSE = 1L;
    private static final Long UPDATED_DOSE = 2L;

    private static final String ENTITY_API_URL = "/api/tomas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private TomaRepository tomaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Toma toma;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Toma createEntity(EntityManager em) {
        Toma toma = new Toma().data(DEFAULT_DATA).lote(DEFAULT_LOTE).dose(DEFAULT_DOSE);
        return toma;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Toma createUpdatedEntity(EntityManager em) {
        Toma toma = new Toma().data(UPDATED_DATA).lote(UPDATED_LOTE).dose(UPDATED_DOSE);
        return toma;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Toma.class).block();
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
        toma = createEntity(em);
    }

    @Test
    void createToma() throws Exception {
        int databaseSizeBeforeCreate = tomaRepository.findAll().collectList().block().size();
        // Create the Toma
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(toma))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeCreate + 1);
        Toma testToma = tomaList.get(tomaList.size() - 1);
        assertThat(testToma.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testToma.getLote()).isEqualTo(DEFAULT_LOTE);
        assertThat(testToma.getDose()).isEqualTo(DEFAULT_DOSE);
    }

    @Test
    void createTomaWithExistingId() throws Exception {
        // Create the Toma with an existing ID
        toma.setId(1L);

        int databaseSizeBeforeCreate = tomaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(toma))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllTomasAsStream() {
        // Initialize the database
        tomaRepository.save(toma).block();

        List<Toma> tomaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Toma.class)
            .getResponseBody()
            .filter(toma::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(tomaList).isNotNull();
        assertThat(tomaList).hasSize(1);
        Toma testToma = tomaList.get(0);
        assertThat(testToma.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testToma.getLote()).isEqualTo(DEFAULT_LOTE);
        assertThat(testToma.getDose()).isEqualTo(DEFAULT_DOSE);
    }

    @Test
    void getAllTomas() {
        // Initialize the database
        tomaRepository.save(toma).block();

        // Get all the tomaList
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
            .value(hasItem(toma.getId().intValue()))
            .jsonPath("$.[*].data")
            .value(hasItem(DEFAULT_DATA.toString()))
            .jsonPath("$.[*].lote")
            .value(hasItem(DEFAULT_LOTE))
            .jsonPath("$.[*].dose")
            .value(hasItem(DEFAULT_DOSE.intValue()));
    }

    @Test
    void getToma() {
        // Initialize the database
        tomaRepository.save(toma).block();

        // Get the toma
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, toma.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(toma.getId().intValue()))
            .jsonPath("$.data")
            .value(is(DEFAULT_DATA.toString()))
            .jsonPath("$.lote")
            .value(is(DEFAULT_LOTE))
            .jsonPath("$.dose")
            .value(is(DEFAULT_DOSE.intValue()));
    }

    @Test
    void getNonExistingToma() {
        // Get the toma
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewToma() throws Exception {
        // Initialize the database
        tomaRepository.save(toma).block();

        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();

        // Update the toma
        Toma updatedToma = tomaRepository.findById(toma.getId()).block();
        updatedToma.data(UPDATED_DATA).lote(UPDATED_LOTE).dose(UPDATED_DOSE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedToma.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedToma))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
        Toma testToma = tomaList.get(tomaList.size() - 1);
        assertThat(testToma.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testToma.getLote()).isEqualTo(UPDATED_LOTE);
        assertThat(testToma.getDose()).isEqualTo(UPDATED_DOSE);
    }

    @Test
    void putNonExistingToma() throws Exception {
        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();
        toma.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, toma.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(toma))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchToma() throws Exception {
        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();
        toma.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(toma))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamToma() throws Exception {
        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();
        toma.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(toma))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTomaWithPatch() throws Exception {
        // Initialize the database
        tomaRepository.save(toma).block();

        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();

        // Update the toma using partial update
        Toma partialUpdatedToma = new Toma();
        partialUpdatedToma.setId(toma.getId());

        partialUpdatedToma.lote(UPDATED_LOTE).dose(UPDATED_DOSE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedToma.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedToma))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
        Toma testToma = tomaList.get(tomaList.size() - 1);
        assertThat(testToma.getData()).isEqualTo(DEFAULT_DATA);
        assertThat(testToma.getLote()).isEqualTo(UPDATED_LOTE);
        assertThat(testToma.getDose()).isEqualTo(UPDATED_DOSE);
    }

    @Test
    void fullUpdateTomaWithPatch() throws Exception {
        // Initialize the database
        tomaRepository.save(toma).block();

        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();

        // Update the toma using partial update
        Toma partialUpdatedToma = new Toma();
        partialUpdatedToma.setId(toma.getId());

        partialUpdatedToma.data(UPDATED_DATA).lote(UPDATED_LOTE).dose(UPDATED_DOSE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedToma.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedToma))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
        Toma testToma = tomaList.get(tomaList.size() - 1);
        assertThat(testToma.getData()).isEqualTo(UPDATED_DATA);
        assertThat(testToma.getLote()).isEqualTo(UPDATED_LOTE);
        assertThat(testToma.getDose()).isEqualTo(UPDATED_DOSE);
    }

    @Test
    void patchNonExistingToma() throws Exception {
        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();
        toma.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, toma.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(toma))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchToma() throws Exception {
        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();
        toma.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(toma))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamToma() throws Exception {
        int databaseSizeBeforeUpdate = tomaRepository.findAll().collectList().block().size();
        toma.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(toma))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Toma in the database
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteToma() {
        // Initialize the database
        tomaRepository.save(toma).block();

        int databaseSizeBeforeDelete = tomaRepository.findAll().collectList().block().size();

        // Delete the toma
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, toma.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Toma> tomaList = tomaRepository.findAll().collectList().block();
        assertThat(tomaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
