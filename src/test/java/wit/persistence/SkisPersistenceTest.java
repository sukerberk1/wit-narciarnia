package wit.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.domain.SkiTiesType;
import wit.domain.Skis;
import wit.domain.SkisType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SkisPersistenceTest {

    private static final Path SKI_FILE = Path.of("src/main/resources/ski.csv");
    private static final Path SKIS_TYPE_FILE = Path.of("src/main/resources/skisType.csv");

    private SkisPersistence skisPersistence;
    private SkisTypePersistence skisTypePersistence;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(SKI_FILE);
        backupFile(SKIS_TYPE_FILE);

        skisPersistence = new SkisPersistence();
        skisTypePersistence = new SkisTypePersistence();
    }

    @AfterEach
    void tearDown() throws IOException {
        restoreFile(SKI_FILE);
        restoreFile(SKIS_TYPE_FILE);
    }

    private void backupFile(Path path) throws IOException {
        if (Files.exists(path)) {
            Path backup = Path.of(path.toString() + ".bak");
            Files.copy(path, backup, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(path);
        }
    }

    private void restoreFile(Path path) throws IOException {
        Path backup = Path.of(path.toString() + ".bak");
        if (Files.exists(backup)) {
            Files.copy(backup, path, StandardCopyOption.REPLACE_EXISTING);
            Files.delete(backup);
        } else {
            Files.deleteIfExists(path);
        }
    }

    private SkisType createAndSaveSkisType() {
        SkisType type = new SkisType("All Mountain", "Versatile choice");
        skisTypePersistence.save(type);
        return type;
    }

    @Test
    void save() {
        SkisType type = createAndSaveSkisType();
        Skis skis = new Skis(type, "Salomon", "QST 92", SkiTiesType.ALPINE, 177.0);

        skisPersistence.save(skis);

        Optional<Skis> savedOpt = skisPersistence.findById(skis.getId());
        assertTrue(savedOpt.isPresent(), "Saved Skis entity should be retrievable");

        Skis saved = savedOpt.get();
        assertEquals(skis.getId(), saved.getId());
        assertEquals(type.getId(), saved.getType().getId());
        assertEquals("Salomon", saved.getBrand());
        assertEquals("QST 92", saved.getModel());
        assertEquals(SkiTiesType.ALPINE, saved.getTies());
        assertEquals(177.0, saved.getLength());
    }

    @Test
    void findAll() {
        SkisType type = createAndSaveSkisType();
        Skis skis1 = new Skis(type, "K2", "Mindbender", SkiTiesType.ALPINE, 179.0);
        Skis skis2 = new Skis(type, "Atomic", "Bent Chetler", SkiTiesType.FREERIDE, 184.0);

        skisPersistence.save(skis1);
        skisPersistence.save(skis2);

        List<Skis> allSkis = skisPersistence.findAll();
        assertEquals(2, allSkis.size(), "Should return both saved Skis entries");
    }

    @Test
    void findById() {
        SkisType type = createAndSaveSkisType();
        Skis skis = new Skis(type, "Head", "Kore 93", SkiTiesType.SKITOUR, 180.0);
        skisPersistence.save(skis);

        Optional<Skis> found = skisPersistence.findById(skis.getId());
        assertTrue(found.isPresent(), "Should locate entity by ID");
        assertEquals("Head", found.get().getBrand());

        Optional<Skis> notFound = skisPersistence.findById(UUID.randomUUID());
        assertFalse(notFound.isPresent(), "Should return empty Optional for unknown ID");
    }

    @Test
    void update() {
        SkisType type = createAndSaveSkisType();
        Skis skis = new Skis(type, "Fischer", "Ranger", SkiTiesType.ALPINE, 172.0);
        skisPersistence.save(skis);

        skis.setBrand("Fischer Updated");
        skis.setModel("Ranger 90");
        skis.setLength(174.0);
        skis.setTies(SkiTiesType.SKITOUR);

        skisPersistence.update(skis);

        Optional<Skis> retrievedOpt = skisPersistence.findById(skis.getId());
        assertTrue(retrievedOpt.isPresent());

        Skis retrieved = retrievedOpt.get();
        assertEquals("Fischer Updated", retrieved.getBrand());
        assertEquals("Ranger 90", retrieved.getModel());
        assertEquals(174.0, retrieved.getLength());
        assertEquals(SkiTiesType.SKITOUR, retrieved.getTies());
    }

    @Test
    void delete() {
        SkisType type = createAndSaveSkisType();
        Skis skis = new Skis(type, "Dynastar", "M-Pro", SkiTiesType.TELEMARK, 182.0);
        skisPersistence.save(skis);
        assertTrue(skisPersistence.findById(skis.getId()).isPresent());

        skisPersistence.delete(skis.getId());
        assertFalse(skisPersistence.findById(skis.getId()).isPresent(), "Entity should be absent post-deletion");
    }
}