package tn.esprit.tpfoyer.service;

import tn.esprit.tpfoyer.entity.Bloc;

import java.util.List;

public interface IBlocService {
    List<Bloc> retrieveAllBlocs();
    Bloc retrieveBloc(Long blocId);
    Bloc addBloc(Bloc bloc);
    void removeBloc(Long blocId);
    Bloc modifyBloc(Bloc bloc);
    Bloc addBlocAndAssignToFoyer(Bloc bloc);
    Bloc assignBlocToFoyer(Long idFoyer, Long idBloc);
    Bloc unassignBlocFromFoyer(Long idBloc);
    List<Bloc> getBlocsByFoyerId(Long foyerId);
}