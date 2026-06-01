package wit.persistence;

import wit.domain.Rentee;
import wit.persistence.common.BasePersistence;

public class RenteePersistence extends BasePersistence<Rentee, String> {

    /**
     * Must return valid path to the CSV database file.
     *
     * @return
     */
    @Override
    protected String getCsvPath() {
        return "src/main/resources/rentee.csv";
    }

    /**
     * Must construct a type from CSV-provided value array;
     *
     * @param csvValues
     * @return
     */
    @Override
    protected Rentee constructFromCsv(String[] csvValues) {
        return new Rentee(csvValues[0], csvValues[1], csvValues[2], csvValues[3]);
    }

    /**
     * Must serialize the entity into the csv line;
     *
     * @param entity
     * @return
     */
    @Override
    protected String createCsvLine(Rentee entity) {
        return String.join(CSV_SEPARATOR,
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDescription()
        );
    }
}