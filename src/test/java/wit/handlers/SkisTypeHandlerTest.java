package wit.handlers;

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

class SkisTypeHandlerTest {

    private static final Path SKIS_TYPE_FILE =
            Path.of("src/main/resources/skisType.csv");

    private SkisTypeHandler skisTypeHandler;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(SKIS_TYPE_FILE);
        skisTypeHandler = new SkisTypeHandler();
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
    void create() {
        SkisType created =
                skisTypeHandler.create("Slalom", "Short racing skis");

        Optional<SkisType> found =
                skisTypeHandler.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
        assertEquals("Slalom", found.get().getName());
        assertEquals("Short racing skis", found.get().getDescription());
    }

    @Test
    void getAll() {
        skisTypeHandler.create("Slalom", "Short turns");
        skisTypeHandler.create("Freeride", "Powder skis");

        List<SkisType> allTypes = skisTypeHandler.getAll();

        assertEquals(2, allTypes.size());
    }

    @Test
    void getById() {
        SkisType created =
                skisTypeHandler.create("All Mountain", "Universal skis");

        Optional<SkisType> found =
                skisTypeHandler.getById(created.getId());

        Optional<SkisType> notFound =
                skisTypeHandler.getById(UUID.randomUUID());

        assertTrue(found.isPresent());
        assertEquals("All Mountain", found.get().getName());
        assertFalse(notFound.isPresent());
    }

    @Test
    void update() {
        SkisType created =
                skisTypeHandler.create("Freeride", "Powder skis");

        SkisType updated = skisTypeHandler.update(
                created.getId(),
                "Freeride Updated",
                "Deep powder skis"
        );

        Optional<SkisType> found =
                skisTypeHandler.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(updated.getId(), found.get().getId());
        assertEquals("Freeride Updated", found.get().getName());
        assertEquals("Deep powder skis", found.get().getDescription());
    }

    @Test
    void delete() {
        SkisType created =
                skisTypeHandler.create("Telemark", "Free heel skiing");

        assertTrue(skisTypeHandler.getById(created.getId()).isPresent());

        skisTypeHandler.delete(created.getId());

        assertFalse(skisTypeHandler.getById(created.getId()).isPresent());
    }
}
