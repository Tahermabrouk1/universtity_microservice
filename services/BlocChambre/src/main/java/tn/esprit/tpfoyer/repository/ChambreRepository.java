package tn.esprit.tpfoyer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.entity.TypeChambre;

import java.util.List;

@Repository
public interface ChambreRepository extends JpaRepository<Chambre, Long> {
    List<Chambre> findAllByTypeC(TypeChambre tc);
    Chambre findChambreByNumeroChambre(Long numc);
//    @Query("select distinct(ch) from Chambre ch " +
//            " inner join ch.reservations reservation " +
//            "inner join reservation.etudiants e " +
//            "where e.cin = :cin ")
//    Chambre retreiveChambreByEtudiantCin(@Param("cin") Long cin);

}
