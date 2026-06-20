package wit.persistence;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.domain.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RentalPersistenceTest {

    private static final Path RENTAL_FILE = Path.of("src/main/resources/rental.csv");
    private static final Path RENTEE_FILE = Path.of("src/main/resources/rentee.csv");
    private static final Path SKIS_FILE = Path.of("src/main/resources/ski.csv");
    private static final Path SKIS_TYPE_FILE = Path.of("src/main/resources/skisType.csv");

    private RentalPersistence rentalPersistence;
    private RenteePersistence renteePersistence;
    private SkisPersistence skisPersistence;
    private SkisTypePersistence skisTypePersistence;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(RENTAL_FILE);
        backupFile(RENTEE_FILE);
        backupFile(SKIS_FILE);
        backupFile(SKIS_TYPE_FILE);

        rentalPersistence = new RentalPersistence();
        renteePersistence = new RenteePersistence();
        skisPersistence = new SkisPersistence();
        skisTypePersistence = new SkisTypePersistence();
    }

    @AfterEach
    void tearDown() throws IOException {
        restoreFile(RENTAL_FILE);
        restoreFile(RENTEE_FILE);
        restoreFile(SKIS_FILE);
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

    private Rentee createAndSaveRentee() {
        Rentee rentee = new Rentee("DOC-12345", "John", "Doe", "Regular customer");
        renteePersistence.save(rentee);
        return rentee;
    }

    private SkisType createAndSaveSkisType() {
        SkisType type = new SkisType("Slalom", "Racing skis");
        skisTypePersistence.save(type);
        return type;
    }

    private Skis createAndSaveSkis() {
        SkisType type = createAndSaveSkisType();
        Skis skis = new Skis(type, "Rossignol", "Hero Elite", SkiTiesType.ALPINE, 165.0);
        skisPersistence.save(skis);
        return skis;
    }

    @Test
    void save() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
        Rental rental = new Rental(rentee, skis, plannedEndDate);

        rentalPersistence.save(rental);

        Optional<Rental> savedRentalOpt = rentalPersistence.findById(rental.getId());
        assertTrue(savedRentalOpt.isPresent(), "Rental should be saved and accessible by ID");
        Rental savedRental = savedRentalOpt.get();

        assertEquals(rental.getId(), savedRental.getId());
        assertEquals(rentee.getId(), savedRental.getRentee().getId());
        assertEquals(skis.getId(), savedRental.getSkis().getId());
        assertEquals(rental.getBeginDate().truncatedTo(ChronoUnit.SECONDS), savedRental.getBeginDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(plannedEndDate, savedRental.getPlannedEndDate().truncatedTo(ChronoUnit.SECONDS));
        assertNull(savedRental.getSecondaryPlannedEndDate());
        assertNull(savedRental.getActualEndDate());
        assertFalse(savedRental.isEnded());
    }

    @Test
    void findAll() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);

        Rental rental1 = new Rental(rentee, skis, plannedEndDate);
        Rental rental2 = new Rental(rentee, skis, plannedEndDate);

        rentalPersistence.save(rental1);
        rentalPersistence.save(rental2);

        List<Rental> allRentals = rentalPersistence.findAll();
        assertEquals(2, allRentals.size(), "Should return both saved rental items");
    }

    @Test
    void findById() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
        Rental rental = new Rental(rentee, skis, plannedEndDate);

        rentalPersistence.save(rental);

        Optional<Rental> found = rentalPersistence.findById(rental.getId());
        assertTrue(found.isPresent(), "Should locate the saved entity by UUID");
        assertEquals(rental.getId(), found.get().getId());

        Optional<Rental> notFound = rentalPersistence.findById(UUID.randomUUID());
        assertFalse(notFound.isPresent(), "Should return empty Optional for unknown ID");
    }

    @Test
    void update() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
        Rental rental = new Rental(rentee, skis, plannedEndDate);

        rentalPersistence.save(rental);

        LocalDateTime secondaryDate = LocalDateTime.now().plusDays(10).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime actualEndDate = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        Rental updatedRental = new Rental(
                rental.getId(),
                rentee,
                skis,
                rental.getBeginDate(),
                plannedEndDate,
                secondaryDate,
                actualEndDate,
                true
        );

        rentalPersistence.update(updatedRental);

        Optional<Rental> retrievedOpt = rentalPersistence.findById(rental.getId());
        assertTrue(retrievedOpt.isPresent());
        Rental retrieved = retrievedOpt.get();

        assertEquals(secondaryDate, retrieved.getSecondaryPlannedEndDate().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(actualEndDate, retrieved.getActualEndDate().truncatedTo(ChronoUnit.SECONDS));
        assertTrue(retrieved.isEnded());
    }

    @Test
    void delete() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.SECONDS);
        Rental rental = new Rental(rentee, skis, plannedEndDate);

        rentalPersistence.save(rental);
        assertTrue(rentalPersistence.findById(rental.getId()).isPresent());

        rentalPersistence.delete(rental.getId());
        assertFalse(rentalPersistence.findById(rental.getId()).isPresent(), "Entity should be absent after deletion");
    }
}