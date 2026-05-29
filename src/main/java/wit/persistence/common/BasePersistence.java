package wit.persistence.common;

import wit.domain.common.EntityBase;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class BasePersistence<T extends EntityBase<TId>, TId> {
    // Configuration
    protected final String CSV_SEPARATOR = ",";

    /**
     * Must return valid path to the CSV database file.
     *
     * @return
     */
    protected abstract String getCsvPath();

    /**
     * Must construct a type from CSV-provided value array;
     *
     * @return
     */
    protected abstract T constructFromCsv(String[] csvValues);

    /**
     * Must serialize the entity into the csv line;
     *
     * @return
     */
    protected abstract String createCsvLine(T entity);

    protected BasePersistence() {
        initFile();
    }

    private void initFile() {
        try {
            File file = new File(getCsvPath());
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize CSV file", e);
        }
    }

    public void create(T rentee) {
        List<T> all = findAll();
        if (all.stream().anyMatch(r -> r.getId().equals(rentee.getId()))) {
            throw new IllegalArgumentException("Rentee with ID " + rentee.getId() + " already exists.");
        }
        all.add(rentee);
        saveAll(all);
    }

    public List<T> findAll() {
        List<T> rentees = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(getCsvPath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(CSV_SEPARATOR);
                if (values.length >= 4) {
                    rentees.add(constructFromCsv(values));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rentees;
    }

    public Optional<T> findById(String id) {
        return findAll().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public void update(T updatedRentee) {
        List<T> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(updatedRentee.getId())) {
                all.set(i, updatedRentee);
                found = true;
                break;
            }
        }
        if (found) {
            saveAll(all);
        } else {
            throw new NoSuchElementException("Rentee not found for update.");
        }
    }

    public void delete(String id) {
        List<T> all = findAll();
        List<T> filtered = all.stream()
                .filter(r -> !r.getId().equals(id))
                .collect(Collectors.toList());

        if (all.size() != filtered.size()) {
            saveAll(filtered);
        }
    }

    // Helper to rewrite the entire file
    private void saveAll(List<T> entities) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(getCsvPath()))) {
            for (T r : entities) {
                String line = createCsvLine(r);
                bw.write(line);
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error saving to CSV", e);
        }
    }

}
