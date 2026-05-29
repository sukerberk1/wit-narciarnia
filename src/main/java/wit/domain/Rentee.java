package wit.domain;

import wit.domain.common.EntityBase;

/**
 * Rentee - a person renting the skis.
 * Id is an number of physical Id document.
 */
public class Rentee extends EntityBase<String> {
    private String firstName;
    private String lastName;
    private String description;

    public Rentee(String idNumber, String firstName, String lastName, String description) {
        super(idNumber);
        this.firstName = firstName;
        this.lastName = lastName;
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return this.id;
    }
}
