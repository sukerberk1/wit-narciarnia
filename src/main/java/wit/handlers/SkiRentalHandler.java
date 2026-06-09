package wit.handlers;

import wit.domain.Rental;
import wit.domain.Rentee;
import wit.domain.Skis;
import wit.persistence.RentalPersistence;
import wit.persistence.RenteePersistence;
import wit.persistence.SkisPersistence;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SkiRentalHandler {
    private final SkisPersistence skisPersistence = new SkisPersistence();
    private final RentalPersistence rentalPersistence = new RentalPersistence();
    private final RenteePersistence renteePersistence = new RenteePersistence();

    /**
     * Handle the rental of the skis.
     *
     * @param renteeId       Identifier of the rentee renting the skis
     * @param skisToRentId   Id of the skis to be rent
     * @param plannedEndDate planned end date of the rental
     * @return The Rental if operation is successful, empty optional otherwise.
     */
    public Optional<Rental> handle(String renteeId, UUID skisToRentId, LocalDateTime plannedEndDate) {
        boolean isRentedNow = rentalPersistence.findAll().stream()
                .filter(o -> skisToRentId.equals(o.getSkis().getId()))
                .anyMatch(o -> !o.isEnded());
        if (isRentedNow)
            return Optional.empty();
        Rentee rentee = renteePersistence.findById(renteeId).get();
        Skis skis = skisPersistence.findById(skisToRentId).get();
        Rental rental = new Rental(rentee, skis, plannedEndDate);
        rentalPersistence.save(rental);
        return Optional.of(rental);
    }

}
