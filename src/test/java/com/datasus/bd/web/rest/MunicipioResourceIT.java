package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Municipio;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.MunicipioRepository;
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
 * Integration tests for the {@link MunicipioResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class MunicipioResourceIT {

    private static final String DEFAULT_MUNICIPIO_NOME = "AAAAAAAAAA";
    private static final String UPDATED_MUNICIPIO_NOME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/municipios";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Municipio municipio;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Municipio createEntity(EntityManager em) {
        Municipio municipio = new Municipio().municipioNome(DEFAULT_MUNICIPIO_NOME);
        return municipio;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Municipio createUpdatedEntity(EntityManager em) {
        Municipio municipio = new Municipio().municipioNome(UPDATED_MUNICIPIO_NOME);
        return municipio;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Municipio.class).block();
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
        municipio = createEntity(em);
    }

    @Test
    void createMunicipio() throws Exception {
        int databaseSizeBeforeCreate = municipioRepository.findAll().collectList().block().size();
        // Create the Municipio
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(municipio))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeCreate + 1);
        Municipio testMunicipio = municipioList.get(municipioList.size() - 1);
        assertThat(testMunicipio.getMunicipioNome()).isEqualTo(DEFAULT_MUNICIPIO_NOME);
    }

    @Test
    void createMunicipioWithExistingId() throws Exception {
        // Create the Municipio with an existing ID
        municipio.setId(1L);

        int databaseSizeBeforeCreate = municipioRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(municipio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllMunicipiosAsStream() {
        // Initialize the database
        municipioRepository.save(municipio).block();

        List<Municipio> municipioList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Municipio.class)
            .getResponseBody()
            .filter(municipio::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(municipioList).isNotNull();
        assertThat(municipioList).hasSize(1);
        Municipio testMunicipio = municipioList.get(0);
        assertThat(testMunicipio.getMunicipioNome()).isEqualTo(DEFAULT_MUNICIPIO_NOME);
    }

    @Test
    void getAllMunicipios() {
        // Initialize the database
        municipioRepository.save(municipio).block();

        // Get all the municipioList
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
            .value(hasItem(municipio.getId().intValue()))
            .jsonPath("$.[*].municipioNome")
            .value(hasItem(DEFAULT_MUNICIPIO_NOME));
    }

    @Test
    void getMunicipio() {
        // Initialize the database
        municipioRepository.save(municipio).block();

        // Get the municipio
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, municipio.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(municipio.getId().intValue()))
            .jsonPath("$.municipioNome")
            .value(is(DEFAULT_MUNICIPIO_NOME));
    }

    @Test
    void getNonExistingMunicipio() {
        // Get the municipio
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewMunicipio() throws Exception {
        // Initialize the database
        municipioRepository.save(municipio).block();

        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();

        // Update the municipio
        Municipio updatedMunicipio = municipioRepository.findById(municipio.getId()).block();
        updatedMunicipio.municipioNome(UPDATED_MUNICIPIO_NOME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedMunicipio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedMunicipio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
        Municipio testMunicipio = municipioList.get(municipioList.size() - 1);
        assertThat(testMunicipio.getMunicipioNome()).isEqualTo(UPDATED_MUNICIPIO_NOME);
    }

    @Test
    void putNonExistingMunicipio() throws Exception {
        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();
        municipio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, municipio.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(municipio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchMunicipio() throws Exception {
        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();
        municipio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(municipio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamMunicipio() throws Exception {
        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();
        municipio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(municipio))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateMunicipioWithPatch() throws Exception {
        // Initialize the database
        municipioRepository.save(municipio).block();

        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();

        // Update the municipio using partial update
        Municipio partialUpdatedMunicipio = new Municipio();
        partialUpdatedMunicipio.setId(municipio.getId());

        partialUpdatedMunicipio.municipioNome(UPDATED_MUNICIPIO_NOME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMunicipio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMunicipio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
        Municipio testMunicipio = municipioList.get(municipioList.size() - 1);
        assertThat(testMunicipio.getMunicipioNome()).isEqualTo(UPDATED_MUNICIPIO_NOME);
    }

    @Test
    void fullUpdateMunicipioWithPatch() throws Exception {
        // Initialize the database
        municipioRepository.save(municipio).block();

        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();

        // Update the municipio using partial update
        Municipio partialUpdatedMunicipio = new Municipio();
        partialUpdatedMunicipio.setId(municipio.getId());

        partialUpdatedMunicipio.municipioNome(UPDATED_MUNICIPIO_NOME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedMunicipio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedMunicipio))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
        Municipio testMunicipio = municipioList.get(municipioList.size() - 1);
        assertThat(testMunicipio.getMunicipioNome()).isEqualTo(UPDATED_MUNICIPIO_NOME);
    }

    @Test
    void patchNonExistingMunicipio() throws Exception {
        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();
        municipio.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, municipio.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(municipio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchMunicipio() throws Exception {
        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();
        municipio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(municipio))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamMunicipio() throws Exception {
        int databaseSizeBeforeUpdate = municipioRepository.findAll().collectList().block().size();
        municipio.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(municipio))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Municipio in the database
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteMunicipio() {
        // Initialize the database
        municipioRepository.save(municipio).block();

        int databaseSizeBeforeDelete = municipioRepository.findAll().collectList().block().size();

        // Delete the municipio
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, municipio.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Municipio> municipioList = municipioRepository.findAll().collectList().block();
        assertThat(municipioList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
