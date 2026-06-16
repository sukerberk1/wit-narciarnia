package wit.handlers;

import wit.domain.SkisType;
import wit.persistence.SkisTypePersistence;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Obsługuje operacje CRUD dotyczące typów nart.
 * Klasa pośredniczy między interfejsem użytkownika
 * a warstwą zapisu danych do pliku CSV.
 */
public class SkisTypeHandler {

    /**
     * Obiekt odpowiedzialny za zapis, odczyt,
     * aktualizację i usuwanie typów nart z pliku CSV.
     */
    private final SkisTypePersistence skisTypePersistence =
            new SkisTypePersistence();

    /**
     * Tworzy i zapisuje nowy typ nart.
     *
     * @param name nazwa typu nart
     * @param description opis typu nart
     * @return utworzony typ nart
     */
    public SkisType create(
        String name,
        String description
    ) {

        SkisType skisType = new SkisType(name, description);
        skisTypePersistence.save(skisType);
        return skisType;
    }

    /**
     * Pobiera wszystkie zapisane typy nart.
     *
     * @return lista wszystkich typów nart
     */
    public List<SkisType> getAll() {
        return skisTypePersistence.findAll();
    }

    /**
     * Wyszukuje typ nart na podstawie jego identyfikatora.
     *
     * @param id identyfikator typu nart
     * @return znaleziony typ nart lub pusty obiekt Optional
     */
    public Optional<SkisType> getById(UUID id) {
        return skisTypePersistence.findById(id);
    }

    /**
     * Aktualizuje nazwę i opis istniejącego typu nart.
     *
     * @param id identyfikator aktualizowanego typu nart
     * @param name nowa nazwa typu nart
     * @param description nowy opis typu nart
     * @return zaktualizowany typ nart
     */
    public SkisType update(UUID id, String name, String description) {
        SkisType updatedSkisType =
                new SkisType(id, name, description);

        skisTypePersistence.update(updatedSkisType);
        return updatedSkisType;
    }

    /**
     * Usuwa typ nart na podstawie jego identyfikatora.
     *
     * @param id identyfikator typu nart przeznaczonego do usunięcia
     */
    public void delete(UUID id) {
        skisTypePersistence.delete(id);
    }
}