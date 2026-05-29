package wit.domain;

import wit.domain.common.EntityBase;

import java.util.UUID;

/**
 * Types of skis.
 */
public class SkisType extends EntityBase<UUID> {
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    protected SkisType() {
        super(UUID.randomUUID());
    }

    public SkisType(UUID id, String name, String description) {
        super(id);
        this.name = name;
        this.description = description;
    }

    public SkisType(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }
}
