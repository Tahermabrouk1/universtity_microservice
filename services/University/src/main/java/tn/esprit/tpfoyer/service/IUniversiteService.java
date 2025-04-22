package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.entity.Universite;

import java.util.List;

public interface IUniversiteService {
     List<Universite> retreiveAllUniversities();
     Universite retrieveUniversity(Long universityId);
     Universite addUniversity(Universite universite);
     void removeUniversity(Long universityId);
     Universite modifyUniversity(Universite universite);
    Universite affectFoyerToUniversite(Long UniversiteId, Long foyerId);
    Universite desaffectFoyerFromUniversite(Long universeId);
}
