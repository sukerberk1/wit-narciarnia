package wit.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.domain.SkisType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SkisTypePersistenceTest {

    private static final Path SKIS_TYPE_FILE = Path.of("src/main/resources/skisType.csv");
    private SkisTypePersistence skisTypePersistence;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(SKIS_TYPE_FILE);
        skisTypePersistence = new SkisTypePersistence();
    }

    @AfterEach
    void tearDown() throws IOException {
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

    @Test
    void save() {
        SkisType skisType = new SkisType("Slalom", "Skis optimized for short, quick turns");

        skisTypePersistence.save(skisType);

        Optional<SkisType> savedOpt = skisTypePersistence.findById(skisType.getId());
        assertTrue(savedOpt.isPresent(), "The saved SkisType should be retrievable");

        SkisType saved = savedOpt.get();
        assertEquals(skisType.getId(), saved.getId());
        assertEquals("Slalom", saved.getName());
        assertEquals("Skis optimized for short, quick turns", saved.getDescription());
    }

    @Test
    void findAll() {
        SkisType type1 = new SkisType("Slalom", "Short turns");
        SkisType type2 = new SkisType("Giant Slalom", "Longer turns");

        skisTypePersistence.save(type1);
        skisTypePersistence.save(type2);

        List<SkisType> list = skisTypePersistence.findAll();
        assertEquals(2, list.size(), "Should return both saved SkisType entities");
    }

    @Test
    void findById() {
        SkisType skisType = new SkisType("All Mountain", "Versatile skis");
        skisTypePersistence.save(skisType);

        Optional<SkisType> found = skisTypePersistence.findById(skisType.getId());
        assertTrue(found.isPresent(), "Should find entity with the correct ID");
        assertEquals("All Mountain", found.get().getName());

        Optional<SkisType> notFound = skisTypePersistence.findById(UUID.randomUUID());
        assertFalse(notFound.isPresent(), "Should return empty for random ID");
    }

    @Test
    void update() {
        SkisType initial = new SkisType("Freeride", "Powder skis");
        skisTypePersistence.save(initial);

        // Reconstruct the updated entity with same ID but modified state
        SkisType updated = new SkisType(initial.getId(), "Freeride Updated", "Deep powder skis");
        skisTypePersistence.update(updated);

        Optional<SkisType> retrievedOpt = skisTypePersistence.findById(initial.getId());
        assertTrue(retrievedOpt.isPresent());

        SkisType retrieved = retrievedOpt.get();
        assertEquals("Freeride Updated", retrieved.getName());
        assertEquals("Deep powder skis", retrieved.getDescription());
    }

    @Test
    void delete() {
        SkisType skisType = new SkisType("Telemark", "Free heel skiing");
        skisTypePersistence.save(skisType);
        assertTrue(skisTypePersistence.findById(skisType.getId()).isPresent());

        skisTypePersistence.delete(skisType.getId());
        assertFalse(skisTypePersistence.findById(skisType.getId()).isPresent(), "Entity should be absent post-deletion");
    }
}