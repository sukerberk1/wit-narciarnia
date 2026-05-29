package wit.persistence;

import wit.domain.SkiTiesType;
import wit.domain.Skis;
import wit.persistence.common.BasePersistence;

import java.util.UUID;

public class SkisPersistence extends BasePersistence<Skis, UUID> {
    private final SkisTypePersistence skisTypePersistence = new SkisTypePersistence();

    /**
     * Must return valid path to the CSV database file.
     *
     * @return
     */
    @Override
    protected String getCsvPath() {
        return "src/main/resources/skis.csv";
    }

    /**
     * Must construct a type from CSV-provided value array;
     *
     * @param csvValues
     * @return
     */
    @Override
    protected Skis constructFromCsv(String[] csvValues) {
        return new Skis(
                UUID.fromString(csvValues[0]),
                skisTypePersistence.findById(csvValues[1]).get(),
                csvValues[2],
                csvValues[3],
                SkiTiesType.getSkiTiesType(csvValues[4]),
                Double.valueOf(csvValues[5])
        );
    }

    /**
     * Must serialize the entity into the csv line;
     *
     * @param entity
     * @return
     */
    @Override
    protected String createCsvLine(Skis entity) {
        return String.join(CSV_SEPARATOR,
                entity.getId().toString(),
                entity.getType().getId().toString(),
                entity.getBrand(),
                entity.getModel(),
                entity.getTies().getValue(),
                entity.getLength().toString()
        );
    }
}