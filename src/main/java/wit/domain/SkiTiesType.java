package wit.domain;

public enum SkiTiesType {
    ALPINE("alpine"),
    SKITOUR("skitour"),
    FREERIDE("freeride"),
    RUNNING("running"),
    TELEMARK("telemark");

    private String value;

    public String getValue() {
        return value;
    }

    private SkiTiesType(String value) {
        this.value = value;
    }

    public static SkiTiesType getSkiTiesType(String value) {
        for (SkiTiesType type : SkiTiesType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
}
