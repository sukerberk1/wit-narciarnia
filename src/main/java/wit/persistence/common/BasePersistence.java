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
    protected final String CSV_SEPARATOR = "%%%";

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

    public void save(T entity) {
        List<T> all = findAll();
        if (all.stream().anyMatch(r -> r.getId().equals(entity.getId()))) {
            throw new IllegalArgumentException("Entity with ID " + entity.getId() + " already exists.");
        }
        all.add(entity);
        saveAll(all);
    }

    public List<T> findAll() {
        List<T> entities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(getCsvPath()))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] values = line.split(CSV_SEPARATOR, -1);
                entities.add(constructFromCsv(values));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entities;
    }

    public Optional<T> findById(TId id) {
        return findAll().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst();
    }

    public void update(T entityToUpdate) {
        List<T> all = findAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId().equals(entityToUpdate.getId())) {
                all.set(i, entityToUpdate);
                found = true;
                break;
            }
        }
        if (found) {
            saveAll(all);
        } else {
            throw new NoSuchElementException("Entity [" + entityToUpdate.getClass().getSimpleName() + "] not found for update.");
        }
    }

    public void delete(TId id) {
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
