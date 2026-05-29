package wit.domain.common;

public abstract class EntityBase<TId> {
    protected TId id;

    protected EntityBase(TId id) {
        this.id = id;
    }

    public TId getId() {
        return id;
    }
}
