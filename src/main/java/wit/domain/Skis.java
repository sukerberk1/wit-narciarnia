package wit.domain;

import wit.domain.common.EntityBase;

import java.util.UUID;

/**
 * Skis - a thing for skiing.
 */
public class Skis extends EntityBase<UUID> {
    private SkisType type;
    private String brand;
    private String model;

    public SkiTiesType getTies() {
        return ties;
    }

    public void setTies(SkiTiesType ties) {
        this.ties = ties;
    }

    private SkiTiesType ties;
    private Double length;

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public SkisType getType() {
        return type;
    }

    public void setType(SkisType type) {
        this.type = type;
    }



    protected Skis() {
        super(UUID.randomUUID());
    }

    public Skis(SkisType type, String brand, String model, SkiTiesType ties, Double length) {
        super(UUID.randomUUID());
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.ties = ties;
        this.length = length;
    }

    public Skis(UUID id, SkisType type, String brand, String model, SkiTiesType ties, Double length) {
        this(type, brand, model, ties, length);
        this.id = id;
    }
}
