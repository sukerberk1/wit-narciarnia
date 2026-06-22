package wit.persistence;

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

class RenteePersistenceTest {

    private static final Path RENTEE_FILE = Path.of("src/main/resources/rentee.csv");
    private RenteePersistence renteePersistence;

    @BeforeEach
    void setUp() throws IOException {
        backupFile(RENTEE_FILE);
        renteePersistence = new RenteePersistence();
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
    void save() {
        Rentee rentee = new Rentee("ID-987654", "Alice", "Smith", "Preferred client");

        renteePersistence.save(rentee);

        Optional<Rentee> savedOpt = renteePersistence.findById(rentee.getId());
        assertTrue(savedOpt.isPresent(), "Saved Rentee should be retrievable by ID");

        Rentee saved = savedOpt.get();
        assertEquals("ID-987654", saved.getId());
        assertEquals("Alice", saved.getFirstName());
        assertEquals("Smith", saved.getLastName());
        assertEquals("Preferred client", saved.getDescription());
    }

    @Test
    void findAll() {
        Rentee rentee1 = new Rentee("ID-100001", "John", "Doe", "First tester");
        Rentee rentee2 = new Rentee("ID-100002", "Jane", "Doe", "Second tester");

        renteePersistence.save(rentee1);
        renteePersistence.save(rentee2);

        List<Rentee> allRentees = renteePersistence.findAll();
        assertEquals(2, allRentees.size(), "Should return both saved Rentee entries");
    }

    @Test
    void findById() {
        Rentee rentee = new Rentee("ID-555555", "Robert", "Johnson", "Casual skier");
        renteePersistence.save(rentee);

        Optional<Rentee> found = renteePersistence.findById("ID-555555");
        assertTrue(found.isPresent(), "Should locate correct entity by ID document number");
        assertEquals("Robert", found.get().getFirstName());

        Optional<Rentee> notFound = renteePersistence.findById("NON_EXISTENT_ID");
        assertFalse(notFound.isPresent(), "Should return empty Optional for an invalid ID");
    }

    @Test
    void update() {
        Rentee rentee = new Rentee("ID-333333", "Michael", "Brown", "Needs instruction");
        renteePersistence.save(rentee);

        rentee.setFirstName("Mike");
        rentee.setLastName("Brown-Smith");
        rentee.setDescription("Experienced skier");

        renteePersistence.update(rentee);

        Optional<Rentee> retrievedOpt = renteePersistence.findById("ID-333333");
        assertTrue(retrievedOpt.isPresent());

        Rentee retrieved = retrievedOpt.get();
        assertEquals("Mike", retrieved.getFirstName());
        assertEquals("Brown-Smith", retrieved.getLastName());
        assertEquals("Experienced skier", retrieved.getDescription());
    }

    @Test
    void delete() {
        Rentee rentee = new Rentee("ID-222222", "Emma", "Wilson", "Needs rental package");
        renteePersistence.save(rentee);
        assertTrue(renteePersistence.findById(rentee.getId()).isPresent());

        renteePersistence.delete(rentee.getId());
        assertFalse(renteePersistence.findById(rentee.getId()).isPresent(), "Entity should be absent post-deletion");
    }
}