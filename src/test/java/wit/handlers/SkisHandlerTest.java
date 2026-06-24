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
import wit.persistence.RenteePersistence;
import wit.persistence.SkisTypePersistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SkisHandlerTest {

    private static final Path SKI_FILE =
            Path.of("src/main/resources/ski.csv");

    private static final Path SKIS_TYPE_FILE =
            Path.of("src/main/resources/skisType.csv");

    private static final Path RENTAL_FILE =
            Path.of("src/main/resources/rental.csv");

    private static final Path RENTEE_FILE =
            Path.of("src/main/resources/rentee.csv");

    private SkisHandler skisHandler;
    private SkisTypePersistence skisTypePersistence;
    private RentalPersistence rentalPersistence;
    private RenteePersistence renteePersistence;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(SKI_FILE);
        backupFile(SKIS_TYPE_FILE);
        backupFile(RENTAL_FILE);
        backupFile(RENTEE_FILE);

        skisHandler = new SkisHandler();
        skisTypePersistence = new SkisTypePersistence();
        rentalPersistence = new RentalPersistence();
        renteePersistence = new RenteePersistence();
    }

    @AfterEach
    void tearDown() throws IOException {
        restoreFile(SKI_FILE);
        restoreFile(SKIS_TYPE_FILE);
        restoreFile(RENTAL_FILE);
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

    private SkisType createAndSaveSkisType() {
        SkisType type = new SkisType("All Mountain", "Universal skis");
        skisTypePersistence.save(type);
        return type;
    }

    private Rentee createAndSaveRentee() {
        Rentee rentee = new Rentee(
                "DOC-12345",
                "Jan",
                "Kowalski",
                "Klient testowy"
        );

        renteePersistence.save(rentee);
        return rentee;
    }

    @Test
    void create() {
        SkisType type = createAndSaveSkisType();

        Skis created = skisHandler.create(
                type,
                "Salomon",
                "QST 92",
                SkiTiesType.ALPINE,
                177.0
        );

        Optional<Skis> found =
                skisHandler.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(created.getId(), found.get().getId());
        assertEquals(type.getId(), found.get().getType().getId());
        assertEquals("Salomon", found.get().getBrand());
        assertEquals("QST 92", found.get().getModel());
        assertEquals(SkiTiesType.ALPINE, found.get().getTies());
        assertEquals(177.0, found.get().getLength());
    }

    @Test
    void getAll() {
        SkisType type = createAndSaveSkisType();

        skisHandler.create(type, "K2", "Mindbender", SkiTiesType.ALPINE, 179.0);
        skisHandler.create(type, "Atomic", "Bent Chetler", SkiTiesType.FREERIDE, 184.0);

        List<Skis> allSkis = skisHandler.getAll();

        assertEquals(2, allSkis.size());
    }

    @Test
    void getById() {
        SkisType type = createAndSaveSkisType();

        Skis created = skisHandler.create(
                type,
                "Head",
                "Kore 93",
                SkiTiesType.SKITOUR,
                180.0
        );

        Optional<Skis> found =
                skisHandler.getById(created.getId());

        Optional<Skis> notFound =
                skisHandler.getById(UUID.randomUUID());

        assertTrue(found.isPresent());
        assertEquals("Head", found.get().getBrand());
        assertFalse(notFound.isPresent());
    }

    @Test
    void update() {
        SkisType initialType = createAndSaveSkisType();

        SkisType updatedType = new SkisType(
                "Freeride",
                "Powder skis"
        );
        skisTypePersistence.save(updatedType);

        Skis created = skisHandler.create(
                initialType,
                "Fischer",
                "Ranger",
                SkiTiesType.ALPINE,
                172.0
        );

        Skis updated = skisHandler.update(
                created.getId(),
                updatedType,
                "Fischer Updated",
                "Ranger 90",
                SkiTiesType.SKITOUR,
                174.0
        );

        Optional<Skis> found =
                skisHandler.getById(created.getId());

        assertTrue(found.isPresent());
        assertEquals(updated.getId(), found.get().getId());
        assertEquals(updatedType.getId(), found.get().getType().getId());
        assertEquals("Fischer Updated", found.get().getBrand());
        assertEquals("Ranger 90", found.get().getModel());
        assertEquals(SkiTiesType.SKITOUR, found.get().getTies());
        assertEquals(174.0, found.get().getLength());
    }

    @Test
    void delete() {
        SkisType type = createAndSaveSkisType();

        Skis created = skisHandler.create(
                type,
                "Dynastar",
                "M-Pro",
                SkiTiesType.TELEMARK,
                182.0
        );

        assertTrue(skisHandler.getById(created.getId()).isPresent());

        skisHandler.delete(created.getId());

        assertFalse(skisHandler.getById(created.getId()).isPresent());
    }

    @Test
    void getAvailableAndGetRented() {
        SkisType type = createAndSaveSkisType();
        Rentee rentee = createAndSaveRentee();

        Skis rentedSkis = skisHandler.create(
                type,
                "Rossignol",
                "Hero Elite",
                SkiTiesType.ALPINE,
                165.0
        );

        Skis availableSkis = skisHandler.create(
                type,
                "Atomic",
                "Redster",
                SkiTiesType.ALPINE,
                170.0
        );

        Rental rental = new Rental(
                rentee,
                rentedSkis,
                LocalDateTime.now().plusDays(7)
        );

        rentalPersistence.save(rental);

        List<Skis> available = skisHandler.getAvailable();
        List<Skis> rented = skisHandler.getRented();

        assertEquals(1, available.size());
        assertEquals(1, rented.size());

        assertEquals(availableSkis.getId(), available.get(0).getId());
        assertEquals(rentedSkis.getId(), rented.get(0).getId());
    }
}
