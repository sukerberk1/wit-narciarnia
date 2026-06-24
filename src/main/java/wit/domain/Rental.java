package wit.domain;

import wit.domain.common.EntityBase;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Encja reprezentująca wypożyczenie
 */
public class Rental extends EntityBase<UUID> {
    private final Rentee rentee;
    private final Skis skis;
    private final LocalDateTime beginDate;
    private final LocalDateTime plannedEndDate;
    private LocalDateTime secondaryPlannedEndDate;
    private LocalDateTime actualEndDate;
    private boolean isEnded;

    /**
     * Recommended constructor
     */
    public Rental(Rentee rentee, Skis skis, LocalDateTime plannedEndDate) {
        super(UUID.randomUUID());
        this.rentee = rentee;
        this.skis = skis;
        this.beginDate = LocalDateTime.now();
        this.plannedEndDate = plannedEndDate;
        this.isEnded = false;
        this.secondaryPlannedEndDate = null;
        this.actualEndDate = null;
    }

    /**
     * Constructor to be able to reconstruct the entity from the persistence
     */
    public Rental(UUID id, Rentee rentee, Skis skis, LocalDateTime beginDate, LocalDateTime plannedEndDate, LocalDateTime secondaryPlannedEndDate, LocalDateTime actualEndDate, boolean isEnded) {
        super(id);
        this.rentee = rentee;
        this.skis = skis;
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

    public Skis getSkis() {
        return skis;
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

    /**
     * Sprawdza czy wypożyczenie jest już zakończone
     *
     * @return
     */
    public boolean isEnded() {
        return isEnded;
    }

    /**
     * Zwraca czy wypożyczenie było już przedłużane
     *
     * @return
     */
    public boolean isProlonged() {
        return secondaryPlannedEndDate != null;
    }

    /**
     * Przedłuża wypożyczenie
     */
    public void prolongRental() {
        if (this.secondaryPlannedEndDate == null)
            this.secondaryPlannedEndDate = plannedEndDate.plusDays(7);
    }

    /**
     * Kończy wypożyczenie.
     */
    public void endRental() {
        this.actualEndDate = LocalDateTime.now();
        this.isEnded = true;
    }
}
