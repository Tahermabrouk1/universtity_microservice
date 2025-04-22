package tn.esprit.tpfoyer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import tn.esprit.tpfoyer.repository.ReservationRepository;
import tn.esprit.tpfoyer.entity.Reservation;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ReservationService implements IReservationService{
    ReservationRepository reservationRepository;
    @Override
    public List<Reservation> retreiveAllReservations() {
        return reservationRepository.findAll();
    }

    @Override
    public Reservation retrieveReservation(String reservationId) {
        return reservationRepository.findById(reservationId).get();
    }

    @Override
    public Reservation addReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Override
    public void removeReservation(String reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    @Override
    public Reservation modifyReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    @Scheduled(fixedRate = 50000)
    public void mettreAJourEtAffciherReservations() {
        List<Reservation> reservations = reservationRepository.findAll();

        String string = "01-01-2024";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy");
        LocalDate localDate = LocalDate.parse(string, formatter);
        java.util.Date d = java.sql.Date.valueOf(localDate);

        for(Reservation r : reservations) {
            if(r.getAnneeUniversitaire().before(d)) {
                r.setEstValide(false);
                reservationRepository.save(r);
            }
            log.info(("repo" + r));

        }
    }
}
