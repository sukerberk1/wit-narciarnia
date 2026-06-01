package wit.domain;

import wit.domain.common.EntityBase;

import java.time.LocalDateTime;
import java.util.UUID;

public class Rental extends EntityBase<UUID> {
    private final Rentee rentee;
    private final LocalDateTime beginDate;
    private final LocalDateTime plannedEndDate;
    private LocalDateTime secondaryPlannedEndDate;
    private LocalDateTime actualEndDate;
    private final boolean isEnded;

    /**
     * Recommended constructor
     */
    public Rental(Rentee rentee, LocalDateTime beginDate, LocalDateTime plannedEndDate) {
        super(UUID.randomUUID());
        this.rentee = rentee;
        this.beginDate = beginDate;
        this.plannedEndDate = plannedEndDate;
        this.isEnded = false;
        this.secondaryPlannedEndDate = null;
        this.actualEndDate = null;
    }

    /**
     * Constructor to be able to reconstruct the entity from the persistence
     */
    public Rental(UUID id, Rentee rentee, LocalDateTime beginDate, LocalDateTime plannedEndDate, LocalDateTime secondaryPlannedEndDate, LocalDateTime actualEndDate, boolean isEnded) {
        super(id);
        this.rentee = rentee;
        this.beginDate = beginDate;
        this.plannedEndDate = plannedEndDate;
        this.secondaryPlannedEndDate = secondaryPlannedEndDate;
        this.actualEndDate = actualEndDate;
        this.isEnded = isEnded;
    }

    public Rentee getRentee() {
        return rentee;
    }

    public LocalDateTime getBeginDate() {
        return beginDate;
    }

    public LocalDateTime getPlannedEndDate() {
        return plannedEndDate;
    }

    public LocalDateTime getSecondaryPlannedEndDate() {
        return secondaryPlannedEndDate;
    }

    public LocalDateTime getActualEndDate() {
        return actualEndDate;
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void prolongRental(LocalDateTime secondaryPlannedEndDate) {
        if (this.secondaryPlannedEndDate == null)
            this.secondaryPlannedEndDate = secondaryPlannedEndDate;
    }

    public void endRental() {
        this.actualEndDate = LocalDateTime.now();
    }
}
