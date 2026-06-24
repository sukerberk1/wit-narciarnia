package wit.handlers;

import wit.domain.SkiTiesType;
import wit.domain.Skis;
import wit.domain.SkisType;
import wit.persistence.SkisPersistence;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import wit.persistence.RentalPersistence;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Obsługuje operacje CRUD dotyczące nart znajdujących się
 * w ewidencji wypożyczalni.
 * Klasa pośredniczy między interfejsem użytkownika
 * a warstwą zapisu danych do pliku CSV.
 */
public class SkisHandler {

        /**
         * Obiekt odpowiedzialny za zapis, odczyt,
         * aktualizację i usuwanie nart z pliku CSV.
         */
        private final SkisPersistence skisPersistence = new SkisPersistence();

        /**
         * Obiekt odpowiedzialny za odczytywanie danych o wypożyczeniach.
         */
        private final RentalPersistence rentalPersistence = new RentalPersistence();

        /**
         * Tworzy i zapisuje nowe narty.
         *
         * @param type   typ nart
         * @param brand  marka nart
         * @param model  model nart
         * @param ties   typ wiązań
         * @param length długość nart
         * @return utworzone narty
         */
        public Skis create(
                        SkisType type,
                        String brand,
                        String model,
                        SkiTiesType ties,
                        Double length) {
                Skis skis = new Skis(type, brand, model, ties, length);
                skisPersistence.save(skis);
                return skis;
        }

        /**
         * Pobiera wszystkie zapisane narty.
         *
         * @return lista wszystkich nart
         */
        public List<Skis> getAll() {
                return skisPersistence.findAll();
        }

        /**
         * Pobiera wszystkie dostępną narty.
         *
         * @return lista wszystkich dostępnych nart
         */
        public List<Skis> getAvailable() {
                Set<UUID> rentedSkisIds = rentalPersistence.findAll().stream()
                                .map(w -> w.getSkis().getId())
                                .collect(Collectors.toSet());

                return skisPersistence.findAll().stream()
                                .filter(skis -> !rentedSkisIds.contains(skis.getId()))
                                .collect(Collectors.toList());
        }

        /**
         * Pobiera narty, które są aktualnie wypożyczone.
         *
         * @return lista wypożyczonych nart
         */
        public List<Skis> getRented() {
                Set<UUID> rentedSkisIds = rentalPersistence.findAll().stream()
                                .filter(rental -> rental.getActualEndDate() == null)
                                .map(rental -> rental.getSkis().getId())
                                .collect(Collectors.toSet());

                return skisPersistence.findAll().stream()
                                .filter(skis -> rentedSkisIds.contains(skis.getId()))
                                .toList();
        }

        /**
         * Wyszukuje narty na podstawie ich identyfikatora.
         *
         * @param id identyfikator nart
         * @return znalezione narty lub pusty obiekt Optional
         */
        public Optional<Skis> getById(UUID id) {
                return skisPersistence.findById(id);
        }

        /**
         * Aktualizuje dane istniejących nart.
         *
         * @param id     identyfikator aktualizowanych nart
         * @param type   nowy typ nart
         * @param brand  nowa marka nart
         * @param model  nowy model nart
         * @param ties   nowy typ wiązań
         * @param length nowa długość nart
         * @return zaktualizowane narty
         */
        public Skis update(
                        UUID id,
                        SkisType type,
                        String brand,
                        String model,
                        SkiTiesType ties,
                        Double length) {
                Skis updatedSkis = new Skis(
                                id,
                                type,
                                brand,
                                model,
                                ties,
                                length);

                skisPersistence.update(updatedSkis);
                return updatedSkis;
        }

        /**
         * Usuwa narty, jeśli nie są aktualnie wypożyczone.
         *
         * @param id identyfikator nart przeznaczonych do usunięcia
         * @return true jeśli usunięto narty, false jeśli narty są aktualnie wypożyczone
         */
        public boolean delete(UUID id) {
                boolean isCurrentlyRented = rentalPersistence.findAll().stream()
                                .anyMatch(rental -> rental.getSkis().getId().equals(id)
                                                && rental.getActualEndDate() == null);

                if (isCurrentlyRented) {
                        return false;
                }

                skisPersistence.delete(id);
                return true;
        }
}