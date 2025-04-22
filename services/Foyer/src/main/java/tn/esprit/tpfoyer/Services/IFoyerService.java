package tn.esprit.tpfoyer.Services;

import tn.esprit.tpfoyer.dto.Bloc;
import tn.esprit.tpfoyer.entity.Foyer;

import java.util.List;

public interface IFoyerService {
    List<Foyer> retrieveAllFoyers();
    Foyer retrieveFoyer(Long foyerId);
    Foyer addFoyer(Foyer foyer);
    void removeFoyer(Long foyerId);
    Foyer modifyFoyer(Foyer foyer);
    Foyer affectBlocToFoyer(Long blocId, Long foyerId);
    Foyer desaffectBlocFromFoyer(Long blocId, Long foyerId);
    List<Bloc> getBlocsByFoyerId(Long foyerId);
    Foyer assignUniversiteToFoyer(Long foyerId, Long universiteId);
    Foyer unassignUniversiteFromFoyer(Long foyerId);
}