package com.datasus.bd.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.datasus.bd.IntegrationTest;
import com.datasus.bd.domain.Uf;
import com.datasus.bd.repository.EntityManager;
import com.datasus.bd.repository.UfRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link UfResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UfResourceIT {

    private static final Long DEFAULT_CODIGO_IBGE = 1L;
    private static final Long UPDATED_CODIGO_IBGE = 2L;

    private static final String DEFAULT_ESTADO = "AAAAAAAAAA";
    private static final String UPDATED_ESTADO = "BBBBBBBBBB";

    private static final byte[] DEFAULT_BANDEIRA = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_BANDEIRA = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_BANDEIRA_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_BANDEIRA_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/ufs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{idUf}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private UfRepository ufRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Uf uf;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Uf createEntity(EntityManager em) {
        Uf uf = new Uf()
            .codigoIbge(DEFAULT_CODIGO_IBGE)
            .estado(DEFAULT_ESTADO)
            .bandeira(DEFAULT_BANDEIRA)
            .bandeiraContentType(DEFAULT_BANDEIRA_CONTENT_TYPE);
        return uf;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Uf createUpdatedEntity(EntityManager em) {
        Uf uf = new Uf()
            .codigoIbge(UPDATED_CODIGO_IBGE)
            .estado(UPDATED_ESTADO)
            .bandeira(UPDATED_BANDEIRA)
            .bandeiraContentType(UPDATED_BANDEIRA_CONTENT_TYPE);
        return uf;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Uf.class).block();
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
        uf = createEntity(em);
    }

    @Test
    void createUf() throws Exception {
        int databaseSizeBeforeCreate = ufRepository.findAll().collectList().block().size();
        // Create the Uf
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uf))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeCreate + 1);
        Uf testUf = ufList.get(ufList.size() - 1);
        assertThat(testUf.getCodigoIbge()).isEqualTo(DEFAULT_CODIGO_IBGE);
        assertThat(testUf.getEstado()).isEqualTo(DEFAULT_ESTADO);
        assertThat(testUf.getBandeira()).isEqualTo(DEFAULT_BANDEIRA);
        assertThat(testUf.getBandeiraContentType()).isEqualTo(DEFAULT_BANDEIRA_CONTENT_TYPE);
    }

    @Test
    void createUfWithExistingId() throws Exception {
        // Create the Uf with an existing ID
        uf.setIdUf(1L);

        int databaseSizeBeforeCreate = ufRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uf))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllUfs() {
        // Initialize the database
        ufRepository.save(uf).block();

        // Get all the ufList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=idUf,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].idUf")
            .value(hasItem(uf.getIdUf().intValue()))
            .jsonPath("$.[*].codigoIbge")
            .value(hasItem(DEFAULT_CODIGO_IBGE.intValue()))
            .jsonPath("$.[*].estado")
            .value(hasItem(DEFAULT_ESTADO))
            .jsonPath("$.[*].bandeiraContentType")
            .value(hasItem(DEFAULT_BANDEIRA_CONTENT_TYPE))
            .jsonPath("$.[*].bandeira")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_BANDEIRA)));
    }

    @Test
    void getUf() {
        // Initialize the database
        ufRepository.save(uf).block();

        // Get the uf
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, uf.getIdUf())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.idUf")
            .value(is(uf.getIdUf().intValue()))
            .jsonPath("$.codigoIbge")
            .value(is(DEFAULT_CODIGO_IBGE.intValue()))
            .jsonPath("$.estado")
            .value(is(DEFAULT_ESTADO))
            .jsonPath("$.bandeiraContentType")
            .value(is(DEFAULT_BANDEIRA_CONTENT_TYPE))
            .jsonPath("$.bandeira")
            .value(is(Base64Utils.encodeToString(DEFAULT_BANDEIRA)));
    }

    @Test
    void getNonExistingUf() {
        // Get the uf
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewUf() throws Exception {
        // Initialize the database
        ufRepository.save(uf).block();

        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();

        // Update the uf
        Uf updatedUf = ufRepository.findById(uf.getIdUf()).block();
        updatedUf
            .codigoIbge(UPDATED_CODIGO_IBGE)
            .estado(UPDATED_ESTADO)
            .bandeira(UPDATED_BANDEIRA)
            .bandeiraContentType(UPDATED_BANDEIRA_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedUf.getIdUf())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedUf))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
        Uf testUf = ufList.get(ufList.size() - 1);
        assertThat(testUf.getCodigoIbge()).isEqualTo(UPDATED_CODIGO_IBGE);
        assertThat(testUf.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testUf.getBandeira()).isEqualTo(UPDATED_BANDEIRA);
        assertThat(testUf.getBandeiraContentType()).isEqualTo(UPDATED_BANDEIRA_CONTENT_TYPE);
    }

    @Test
    void putNonExistingUf() throws Exception {
        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();
        uf.setIdUf(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, uf.getIdUf())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uf))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUf() throws Exception {
        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();
        uf.setIdUf(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uf))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUf() throws Exception {
        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();
        uf.setIdUf(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(uf))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUfWithPatch() throws Exception {
        // Initialize the database
        ufRepository.save(uf).block();

        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();

        // Update the uf using partial update
        Uf partialUpdatedUf = new Uf();
        partialUpdatedUf.setIdUf(uf.getIdUf());

        partialUpdatedUf.bandeira(UPDATED_BANDEIRA).bandeiraContentType(UPDATED_BANDEIRA_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUf.getIdUf())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUf))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
        Uf testUf = ufList.get(ufList.size() - 1);
        assertThat(testUf.getCodigoIbge()).isEqualTo(DEFAULT_CODIGO_IBGE);
        assertThat(testUf.getEstado()).isEqualTo(DEFAULT_ESTADO);
        assertThat(testUf.getBandeira()).isEqualTo(UPDATED_BANDEIRA);
        assertThat(testUf.getBandeiraContentType()).isEqualTo(UPDATED_BANDEIRA_CONTENT_TYPE);
    }

    @Test
    void fullUpdateUfWithPatch() throws Exception {
        // Initialize the database
        ufRepository.save(uf).block();

        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();

        // Update the uf using partial update
        Uf partialUpdatedUf = new Uf();
        partialUpdatedUf.setIdUf(uf.getIdUf());

        partialUpdatedUf
            .codigoIbge(UPDATED_CODIGO_IBGE)
            .estado(UPDATED_ESTADO)
            .bandeira(UPDATED_BANDEIRA)
            .bandeiraContentType(UPDATED_BANDEIRA_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUf.getIdUf())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedUf))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
        Uf testUf = ufList.get(ufList.size() - 1);
        assertThat(testUf.getCodigoIbge()).isEqualTo(UPDATED_CODIGO_IBGE);
        assertThat(testUf.getEstado()).isEqualTo(UPDATED_ESTADO);
        assertThat(testUf.getBandeira()).isEqualTo(UPDATED_BANDEIRA);
        assertThat(testUf.getBandeiraContentType()).isEqualTo(UPDATED_BANDEIRA_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingUf() throws Exception {
        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();
        uf.setIdUf(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, uf.getIdUf())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(uf))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUf() throws Exception {
        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();
        uf.setIdUf(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(uf))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUf() throws Exception {
        int databaseSizeBeforeUpdate = ufRepository.findAll().collectList().block().size();
        uf.setIdUf(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(uf))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Uf in the database
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUf() {
        // Initialize the database
        ufRepository.save(uf).block();

        int databaseSizeBeforeDelete = ufRepository.findAll().collectList().block().size();

        // Delete the uf
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, uf.getIdUf())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Uf> ufList = ufRepository.findAll().collectList().block();
        assertThat(ufList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
