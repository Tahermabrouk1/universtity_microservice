package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.entity.Reservation;

import java.util.List;

public interface IReservationService {
     List<Reservation> retreiveAllReservations();
     Reservation retrieveReservation(String reservationId);
     Reservation addReservation(Reservation reservation);
     void removeReservation(String reservationId);
     Reservation modifyReservation(Reservation reservation);

}
