package wit.persistence;

import wit.domain.Rental;
import wit.persistence.common.BasePersistence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class RentalPersistence extends BasePersistence<Rental, UUID> {
    private final RenteePersistence renteePersistence = new RenteePersistence();
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
                LocalDateTime.parse(csvValues[2], FORMATTER),
                LocalDateTime.parse(csvValues[3], FORMATTER),
                LocalDateTime.parse(csvValues[4], FORMATTER),
                LocalDateTime.parse(csvValues[5], FORMATTER),
                Boolean.parseBoolean(csvValues[6])
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
                entity.getBeginDate().format(FORMATTER),
                entity.getPlannedEndDate().format(FORMATTER),
                entity.getSecondaryPlannedEndDate().format(FORMATTER),
                entity.getActualEndDate().format(FORMATTER),
                Boolean.toString(entity.isEnded())
        );
    }
}