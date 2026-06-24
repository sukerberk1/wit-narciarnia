package wit.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.domain.Rentee;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RenteeHandlerTest {

    private static final Path RENTEE_FILE =
            Path.of("src/main/resources/rentee.csv");

    private RenteeHandler renteeHandler;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(RENTEE_FILE);
        renteeHandler = new RenteeHandler();
    }

    @AfterEach
    void tearDown() throws IOException {
        restoreFile(RENTEE_FILE);
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
        Rentee created = renteeHandler.create(
                "DOC-10001",
                "Jan",
                "Kowalski",
                "Stały klient"
        );

        Optional<Rentee> found =
                renteeHandler.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals("DOC-10001", found.get().getId());
        assertEquals("Jan", found.get().getFirstName());
        assertEquals("Kowalski", found.get().getLastName());
        assertEquals("Stały klient", found.get().getDescription());
    }

    @Test
    void getAll() {
        renteeHandler.create("DOC-10001", "Jan", "Kowalski", "Pierwszy klient");
        renteeHandler.create("DOC-10002", "Anna", "Nowak", "Drugi klient");

        List<Rentee> allRentees = renteeHandler.getAll();

        assertEquals(2, allRentees.size());
    }

    @Test
    void getById() {
        renteeHandler.create(
                "DOC-55555",
                "Robert",
                "Lewandowski",
                "Klient testowy"
        );

        Optional<Rentee> found =
                renteeHandler.getById("DOC-55555");

        Optional<Rentee> notFound =
                renteeHandler.getById("UNKNOWN");

        assertTrue(found.isPresent());
        assertEquals("Robert", found.get().getFirstName());
        assertFalse(notFound.isPresent());
    }

    @Test
    void update() {
        renteeHandler.create(
                "DOC-33333",
                "Michał",
                "Zawadzki",
                "Opis przed zmianą"
        );

        Rentee updated = renteeHandler.update(
                "DOC-33333",
                "Michał",
                "Zawadzki-Updated",
                "Opis po zmianie"
        );

        Optional<Rentee> found =
                renteeHandler.getById("DOC-33333");

        assertTrue(found.isPresent());
        assertEquals(updated.getId(), found.get().getId());
        assertEquals("Michał", found.get().getFirstName());
        assertEquals("Zawadzki-Updated", found.get().getLastName());
        assertEquals("Opis po zmianie", found.get().getDescription());
    }

    @Test
    void delete() {
        Rentee created = renteeHandler.create(
                "DOC-22222",
                "Adam",
                "Testowy",
                "Do usunięcia"
        );

        assertTrue(renteeHandler.getById(created.getId()).isPresent());

        renteeHandler.delete(created.getId());

        assertFalse(renteeHandler.getById(created.getId()).isPresent());
    }
}
