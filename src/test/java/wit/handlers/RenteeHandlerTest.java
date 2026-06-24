package wit.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.domain.Rental;
import wit.domain.Rentee;
import wit.domain.SkiTiesType;
import wit.domain.Skis;
import wit.domain.SkisType;
import wit.persistence.RentalPersistence;
import wit.persistence.SkisPersistence;
import wit.persistence.SkisTypePersistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class RenteeHandlerTest {

    private static final Path RENTEE_FILE =
            Path.of("src/main/resources/rentee.csv");

    private static final Path SKI_FILE =
            Path.of("src/main/resources/ski.csv");

    private static final Path SKIS_TYPE_FILE =
            Path.of("src/main/resources/skiType.csv");

    private static final Path RENTAL_FILE =
            Path.of("src/main/resources/rental.csv");

    private RenteeHandler renteeHandler;
    private RentalPersistence rentalPersistence;
    private SkisPersistence skisPersistence;
    private SkisTypePersistence skisTypePersistence;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(RENTEE_FILE);
        backupFile(SKI_FILE);
        backupFile(SKIS_TYPE_FILE);
        backupFile(RENTAL_FILE);

        renteeHandler = new RenteeHandler();
        rentalPersistence = new RentalPersistence();
        skisPersistence = new SkisPersistence();
        skisTypePersistence = new SkisTypePersistence();
    }

    @AfterEach
    void tearDown() throws IOException {
        restoreFile(RENTEE_FILE);
        restoreFile(SKI_FILE);
        restoreFile(SKIS_TYPE_FILE);
        restoreFile(RENTAL_FILE);
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

        boolean deleted = renteeHandler.delete(created.getId());

        assertTrue(deleted);
        assertFalse(renteeHandler.getById(created.getId()).isPresent());
    }

    @Test
    void deleteShouldReturnFalseWhenRenteeHasActiveRental() {
        Rentee rentee = renteeHandler.create(
                "DOC-77777",
                "Jan",
                "Aktywny",
                "Klient z aktywnym wypożyczeniem"
        );

        SkisType type = new SkisType(
                "All Mountain",
                "Universal skis"
        );
        skisTypePersistence.save(type);

        Skis skis = new Skis(
                type,
                "Atomic",
                "Redster",
                SkiTiesType.ALPINE,
                170.0
        );
        skisPersistence.save(skis);

        Rental rental = new Rental(
                rentee,
                skis,
                LocalDateTime.now().plusDays(1)
        );
        rentalPersistence.save(rental);

        boolean deleted = renteeHandler.delete(rentee.getId());

        assertFalse(deleted);
        assertTrue(renteeHandler.getById(rentee.getId()).isPresent());
    }
}
