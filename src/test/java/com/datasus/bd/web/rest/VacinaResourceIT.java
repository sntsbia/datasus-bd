package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Vacina;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.VacinaRepository;
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
 * Integration tests for the {@link VacinaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class VacinaResourceIT {

    private static final String DEFAULT_FABRICANTE = "AAAAAAAAAA";
    private static final String UPDATED_FABRICANTE = "BBBBBBBBBB";

    private static final String DEFAULT_NOME_VACINA = "AAAAAAAAAA";
    private static final String UPDATED_NOME_VACINA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/vacinas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VacinaRepository vacinaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Vacina vacina;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vacina createEntity(EntityManager em) {
        Vacina vacina = new Vacina().fabricante(DEFAULT_FABRICANTE).nomeVacina(DEFAULT_NOME_VACINA);
        return vacina;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Vacina createUpdatedEntity(EntityManager em) {
        Vacina vacina = new Vacina().fabricante(UPDATED_FABRICANTE).nomeVacina(UPDATED_NOME_VACINA);
        return vacina;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Vacina.class).block();
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
        vacina = createEntity(em);
    }

    @Test
    void createVacina() throws Exception {
        int databaseSizeBeforeCreate = vacinaRepository.findAll().collectList().block().size();
        // Create the Vacina
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vacina))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeCreate + 1);
        Vacina testVacina = vacinaList.get(vacinaList.size() - 1);
        assertThat(testVacina.getFabricante()).isEqualTo(DEFAULT_FABRICANTE);
        assertThat(testVacina.getNomeVacina()).isEqualTo(DEFAULT_NOME_VACINA);
    }

    @Test
    void createVacinaWithExistingId() throws Exception {
        // Create the Vacina with an existing ID
        vacina.setId(1L);

        int databaseSizeBeforeCreate = vacinaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vacina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllVacinasAsStream() {
        // Initialize the database
        vacinaRepository.save(vacina).block();

        List<Vacina> vacinaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Vacina.class)
            .getResponseBody()
            .filter(vacina::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(vacinaList).isNotNull();
        assertThat(vacinaList).hasSize(1);
        Vacina testVacina = vacinaList.get(0);
        assertThat(testVacina.getFabricante()).isEqualTo(DEFAULT_FABRICANTE);
        assertThat(testVacina.getNomeVacina()).isEqualTo(DEFAULT_NOME_VACINA);
    }

    @Test
    void getAllVacinas() {
        // Initialize the database
        vacinaRepository.save(vacina).block();

        // Get all the vacinaList
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
            .value(hasItem(vacina.getId().intValue()))
            .jsonPath("$.[*].fabricante")
            .value(hasItem(DEFAULT_FABRICANTE))
            .jsonPath("$.[*].nomeVacina")
            .value(hasItem(DEFAULT_NOME_VACINA));
    }

    @Test
    void getVacina() {
        // Initialize the database
        vacinaRepository.save(vacina).block();

        // Get the vacina
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, vacina.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(vacina.getId().intValue()))
            .jsonPath("$.fabricante")
            .value(is(DEFAULT_FABRICANTE))
            .jsonPath("$.nomeVacina")
            .value(is(DEFAULT_NOME_VACINA));
    }

    @Test
    void getNonExistingVacina() {
        // Get the vacina
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewVacina() throws Exception {
        // Initialize the database
        vacinaRepository.save(vacina).block();

        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();

        // Update the vacina
        Vacina updatedVacina = vacinaRepository.findById(vacina.getId()).block();
        updatedVacina.fabricante(UPDATED_FABRICANTE).nomeVacina(UPDATED_NOME_VACINA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedVacina.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedVacina))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
        Vacina testVacina = vacinaList.get(vacinaList.size() - 1);
        assertThat(testVacina.getFabricante()).isEqualTo(UPDATED_FABRICANTE);
        assertThat(testVacina.getNomeVacina()).isEqualTo(UPDATED_NOME_VACINA);
    }

    @Test
    void putNonExistingVacina() throws Exception {
        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();
        vacina.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, vacina.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vacina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchVacina() throws Exception {
        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();
        vacina.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vacina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamVacina() throws Exception {
        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();
        vacina.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(vacina))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateVacinaWithPatch() throws Exception {
        // Initialize the database
        vacinaRepository.save(vacina).block();

        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();

        // Update the vacina using partial update
        Vacina partialUpdatedVacina = new Vacina();
        partialUpdatedVacina.setId(vacina.getId());

        partialUpdatedVacina.fabricante(UPDATED_FABRICANTE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVacina.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVacina))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
        Vacina testVacina = vacinaList.get(vacinaList.size() - 1);
        assertThat(testVacina.getFabricante()).isEqualTo(UPDATED_FABRICANTE);
        assertThat(testVacina.getNomeVacina()).isEqualTo(DEFAULT_NOME_VACINA);
    }

    @Test
    void fullUpdateVacinaWithPatch() throws Exception {
        // Initialize the database
        vacinaRepository.save(vacina).block();

        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();

        // Update the vacina using partial update
        Vacina partialUpdatedVacina = new Vacina();
        partialUpdatedVacina.setId(vacina.getId());

        partialUpdatedVacina.fabricante(UPDATED_FABRICANTE).nomeVacina(UPDATED_NOME_VACINA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedVacina.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedVacina))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
        Vacina testVacina = vacinaList.get(vacinaList.size() - 1);
        assertThat(testVacina.getFabricante()).isEqualTo(UPDATED_FABRICANTE);
        assertThat(testVacina.getNomeVacina()).isEqualTo(UPDATED_NOME_VACINA);
    }

    @Test
    void patchNonExistingVacina() throws Exception {
        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();
        vacina.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, vacina.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vacina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchVacina() throws Exception {
        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();
        vacina.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vacina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamVacina() throws Exception {
        int databaseSizeBeforeUpdate = vacinaRepository.findAll().collectList().block().size();
        vacina.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(vacina))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Vacina in the database
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteVacina() {
        // Initialize the database
        vacinaRepository.save(vacina).block();

        int databaseSizeBeforeDelete = vacinaRepository.findAll().collectList().block().size();

        // Delete the vacina
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, vacina.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Vacina> vacinaList = vacinaRepository.findAll().collectList().block();
        assertThat(vacinaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
