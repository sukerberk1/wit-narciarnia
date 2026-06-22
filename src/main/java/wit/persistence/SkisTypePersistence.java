package wit.persistence;

import wit.domain.SkisType;
import wit.persistence.common.BasePersistence;

import java.util.UUID;

public class SkisTypePersistence extends BasePersistence<SkisType, UUID> {
    /**
     * Must return valid path to the CSV database file.
     *
     * @return
     */
    @Override
    protected String getCsvPath() {
        return "src/main/resources/skisType.csv";
    }

    /**
     * Must construct a type from CSV-provided value array;
     *
     * @param csvValues
     * @return
     */
    @Override
    protected SkisType constructFromCsv(String[] csvValues) {
        return new SkisType(UUID.fromString(csvValues[0]), csvValues[1], csvValues[2]);
    }

    /**
     * Must serialize the entity into the csv line;
     *
     * @param entity
     * @return
     */
    @Override
    protected String createCsvLine(SkisType entity) {
        return String.join(CSV_SEPARATOR,
                entity.getId().toString(),
                entity.getName(),
                entity.getDescription()
        );
    }
}
