package wit.persistence;

import wit.domain.Rental;
import wit.persistence.common.BasePersistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Repozytorium wypożyczeń
 */
public class RentalPersistence extends BasePersistence<Rental, UUID> {
    private final RenteePersistence renteePersistence = new RenteePersistence();
    private final SkisPersistence skisPersistence = new SkisPersistence();
    private final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Must return valid path to the CSV database file.
     *
     * @return
     */
    @Override
    protected String getCsvPath() {
        return "src/main/resources/rental.csv";
    }

    /**
     * Must construct a type from CSV-provided value array;
     *
     * @param csvValues
     * @return
     */
    @Override
    protected Rental constructFromCsv(String[] csvValues) {
        return new Rental(
                UUID.fromString(csvValues[0]),
                renteePersistence.findById(csvValues[1]).get(),
                skisPersistence.findById(UUID.fromString(csvValues[2])).get(),
                LocalDateTime.parse(csvValues[3], FORMATTER),
                LocalDateTime.parse(csvValues[4], FORMATTER),
                parseNullableDate(csvValues[5]),
                parseNullableDate(csvValues[6]),
                Boolean.parseBoolean(csvValues[7])
        );
    }

    /**
     * Must serialize the entity into the csv line;
     *
     * @param entity
     * @return
     */
    @Override
    protected String createCsvLine(Rental entity) {
        return String.join(CSV_SEPARATOR,
                entity.getId().toString(),
                entity.getRentee().getId(),
                entity.getSkis().getId().toString(),
                entity.getBeginDate().format(FORMATTER),
                entity.getPlannedEndDate().format(FORMATTER),
                formatNullableDate(entity.getSecondaryPlannedEndDate()),
                formatNullableDate(entity.getActualEndDate()),
                Boolean.toString(entity.isEnded())
        );
    }

    /**
    * Zamienia datę na tekst przeznaczony do zapisu w pliku CSV.
    * Dla pustej daty zwraca pusty tekst.
     *
     * @param date zapisywana data lub null
     * @return data w formacie tekstowym albo pusty tekst
    */

    private String formatNullableDate(LocalDateTime date) {
        return date != null ? date.format(FORMATTER) : "";
    }

    /**
     * Zamienia tekst z pliku CSV na datę.
     * Dla pustego tekstu zwraca null.
     *
     * @param dateStr tekst reprezentujący datę lub pusty tekst
     * @return obiekt LocalDateTime albo null
     */

    private LocalDateTime parseNullableDate(String dateStr) {
        return dateStr != null && !dateStr.isEmpty() ? LocalDateTime.parse(dateStr, FORMATTER) : null;
    }
}