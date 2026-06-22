package wit.handlers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.domain.*;
import wit.persistence.RentalPersistence;
import wit.persistence.RenteePersistence;
import wit.persistence.SkisPersistence;
import wit.persistence.SkisTypePersistence;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SkiRentalHandlerTest {

    private static final Path RENTAL_FILE = Path.of("src/main/resources/rental.csv");
    private static final Path RENTEE_FILE = Path.of("src/main/resources/rentee.csv");
    private static final Path SKI_FILE = Path.of("src/main/resources/ski.csv");
    private static final Path SKIS_TYPE_FILE = Path.of("src/main/resources/skisType.csv");

    private SkiRentalHandler handler;
    private RentalPersistence rentalPersistence;
    private RenteePersistence renteePersistence;
    private SkisPersistence skisPersistence;
    private SkisTypePersistence skisTypePersistence;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(RENTAL_FILE);
        backupFile(RENTEE_FILE);
        backupFile(SKI_FILE);
        backupFile(SKIS_TYPE_FILE);

        handler = new SkiRentalHandler();
        rentalPersistence = new RentalPersistence();
        renteePersistence = new RenteePersistence();
        skisPersistence = new SkisPersistence();
        skisTypePersistence = new SkisTypePersistence();
    }

    @AfterEach
    void tearDown() throws IOException {
        restoreFile(RENTAL_FILE);
        restoreFile(RENTEE_FILE);
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

    private Rentee createAndSaveRentee() {
        Rentee rentee = new Rentee("DOC-9999", "Sarah", "Connor", "VIP Client");
        renteePersistence.save(rentee);
        return rentee;
    }

    private Skis createAndSaveSkis() {
        SkisType type = new SkisType("All Mountain", "Versatile skis");
        skisTypePersistence.save(type);
        Skis skis = new Skis(type, "Salomon", "QST 92", SkiTiesType.ALPINE, 177.0);
        skisPersistence.save(skis);
        return skis;
    }

    @Test
    void handleRent_Success() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.SECONDS);

        Optional<Rental> rentalOpt = handler.handleRent(rentee.getId(), skis.getId(), plannedEndDate);

        assertTrue(rentalOpt.isPresent(), "The rental should be successfully processed");
        Rental rental = rentalOpt.get();

        assertEquals(rentee.getId(), rental.getRentee().getId());
        assertEquals(skis.getId(), rental.getSkis().getId());
        assertEquals(plannedEndDate, rental.getPlannedEndDate().truncatedTo(ChronoUnit.SECONDS));
        assertFalse(rental.isEnded());

        // Verify the rental is persisted correctly
        Optional<Rental> savedRental = rentalPersistence.findById(rental.getId());
        assertTrue(savedRental.isPresent());
    }

    @Test
    void handleRent_Failure_AlreadyRented() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(5);

        // Initiate first active rental
        Optional<Rental> firstRental = handler.handleRent(rentee.getId(), skis.getId(), plannedEndDate);
        assertTrue(firstRental.isPresent());

        // Attempt a second active rental on the same skis
        Optional<Rental> secondRental = handler.handleRent(rentee.getId(), skis.getId(), plannedEndDate);
        assertFalse(secondRental.isPresent(), "Should not allow active rental on already-rented skis");
    }

    @Test
    void handleProlongRental() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.SECONDS);

        Optional<Rental> rentalOpt = handler.handleRent(rentee.getId(), skis.getId(), plannedEndDate);
        assertTrue(rentalOpt.isPresent());
        UUID rentalId = rentalOpt.get().getId();

        // Perform prolongation
        Rental prolongedRental = handler.handleProlongRental(rentalId);
        assertNotNull(prolongedRental);

        // Verify changes are updated in persistence
        Optional<Rental> retrievedOpt = rentalPersistence.findById(rentalId);
        assertTrue(retrievedOpt.isPresent());
        assertNotNull(retrievedOpt.get().getSecondaryPlannedEndDate());
    }

    @Test
    void endRental() {
        Rentee rentee = createAndSaveRentee();
        Skis skis = createAndSaveSkis();
        LocalDateTime plannedEndDate = LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.SECONDS);

        Optional<Rental> rentalOpt = handler.handleRent(rentee.getId(), skis.getId(), plannedEndDate);
        assertTrue(rentalOpt.isPresent());
        UUID rentalId = rentalOpt.get().getId();

        // End the rental
        Rental endedRental = handler.endRental(rentalId);
        assertNotNull(endedRental);
        assertNotNull(endedRental.getActualEndDate());

        // Verify changes are updated in persistence
        Optional<Rental> retrievedOpt = rentalPersistence.findById(rentalId);
        assertTrue(retrievedOpt.isPresent());
        assertNotNull(retrievedOpt.get().getActualEndDate());
    }
}