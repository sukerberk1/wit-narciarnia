package wit.handlers;

import wit.domain.Rentee;
import wit.persistence.RenteePersistence;
import java.util.List;
import java.util.Optional;
import wit.persistence.RentalPersistence;

/**
 * Obsługuje operacje CRUD dotyczące klientów wypożyczalni.
 * Klasa pośredniczy między interfejsem użytkownika
 * a warstwą zapisu danych do pliku CSV.
 */
public class RenteeHandler {

    /**
     * Obiekt odpowiedzialny za zapis, odczyt,
     * aktualizację i usuwanie klientów z pliku CSV.
     */
    private final RenteePersistence renteePersistence = new RenteePersistence();

    /**
     * Obiekt odpowiedzialny za odczytywanie danych o wypożyczeniach.
     */
    private final RentalPersistence rentalPersistence = new RentalPersistence();

    /**
     * Tworzy i zapisuje nowego klienta wypożyczalni.
     *
     * @param idNumber    numer dokumentu klienta
     * @param firstName   imię klienta
     * @param lastName    nazwisko klienta
     * @param description dodatkowy opis klienta
     * @return utworzony klient
     */
    public Rentee create(
            String idNumber,
            String firstName,
            String lastName,
            String description) {
        Rentee rentee = new Rentee(idNumber, firstName, lastName, description);

        renteePersistence.save(rentee);
        return rentee;
    }

    /**
     * Pobiera wszystkich zapisanych klientów.
     *
     * @return lista wszystkich klientów
     */
    public List<Rentee> getAll() {
        return renteePersistence.findAll();
    }

    /**
     * Wyszukuje klienta na podstawie numeru dokumentu.
     *
     * @param idNumber numer dokumentu klienta
     * @return znaleziony klient lub pusty obiekt Optional
     */
    public Optional<Rentee> getById(String idNumber) {
        return renteePersistence.findById(idNumber);
    }

    /**
     * Aktualizuje dane istniejącego klienta.
     *
     * @param idNumber    numer dokumentu aktualizowanego klienta
     * @param firstName   nowe imię klienta
     * @param lastName    nowe nazwisko klienta
     * @param description nowy opis klienta
     * @return zaktualizowany klient
     */
    public Rentee update(
            String idNumber,
            String firstName,
            String lastName,
            String description) {
        Rentee updatedRentee = new Rentee(idNumber, firstName, lastName, description);

        renteePersistence.update(updatedRentee);
        return updatedRentee;
    }

    /**
     * Usuwa klienta, jeśli nie ma aktywnego wypożyczenia.
     *
     * @param idNumber numer dokumentu klienta przeznaczonego do usunięcia
     * @return true jeśli usunięto klienta, false jeśli klient ma aktywne
     *         wypożyczenie
     */
    public boolean delete(String idNumber) {
        boolean hasActiveRental = rentalPersistence.findAll().stream()
                .anyMatch(rental -> rental.getRentee().getId().equals(idNumber)
                        && rental.getActualEndDate() == null);

        if (hasActiveRental) {
            return false;
        }

        renteePersistence.delete(idNumber);
        return true;
    }
}