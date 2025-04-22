package tn.esprit.tpfoyer.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpfoyer.Feign.FoyerClient;
import tn.esprit.tpfoyer.dto.AssignBlocToFoyerRequest;
import tn.esprit.tpfoyer.dto.Foyer;
import tn.esprit.tpfoyer.entity.Bloc;
import tn.esprit.tpfoyer.repository.BlocRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class BlocServiceImpl implements IBlocService {

    private final BlocRepository blocRepository;
    private final FoyerClient foyerClient;

    @Override
    public List<Bloc> retrieveAllBlocs() {
        log.info("Retrieving all blocs");
        return blocRepository.findAll();
    }

    @Override
    public Bloc retrieveBloc(Long blocId) {
        if (blocId == null || blocId <= 0) {
            log.error("Invalid bloc ID: {}", blocId);
            throw new IllegalArgumentException("Invalid bloc ID: " + blocId);
        }
        log.info("Retrieving bloc with ID: {}", blocId);
        return blocRepository.findById(blocId)
                .orElseThrow(() -> new EntityNotFoundException("Bloc not found with id: " + blocId));
    }

    @Override
    public Bloc addBloc(Bloc bloc) {
        if (bloc == null || bloc.getNomBloc() == null || bloc.getNomBloc().trim().isEmpty()) {
            log.error("Invalid bloc data: {}", bloc);
            throw new IllegalArgumentException("Bloc name cannot be null or empty");
        }
        log.info("Adding bloc: {}", bloc.getNomBloc());
        return blocRepository.save(bloc);
    }

    @Override
    @Transactional
    public void removeBloc(Long blocId) {
        if (blocId == null || blocId <= 0) {
            log.error("Invalid bloc ID for deletion: {}", blocId);
            throw new IllegalArgumentException("Invalid bloc ID: " + blocId);
        }
        if (!blocRepository.existsById(blocId)) {
            throw new EntityNotFoundException("Bloc not found with id: " + blocId);
        }
        log.info("Removing bloc with ID: {}", blocId);
        blocRepository.deleteById(blocId);
    }

    @Override
    public Bloc modifyBloc(Bloc bloc) {
        if (bloc == null || bloc.getIdBloc() == null || !blocRepository.existsById(bloc.getIdBloc())) {
            log.error("Invalid bloc for modification: {}", bloc);
            throw new EntityNotFoundException("Bloc not found with id: " + (bloc != null ? bloc.getIdBloc() : "null"));
        }
        log.info("Modifying bloc: {}", bloc.getNomBloc());
        return blocRepository.save(bloc);
    }

    @Override
    @Transactional
    public Bloc addBlocAndAssignToFoyer(Bloc bloc) {
        if (bloc == null || bloc.getNomBloc() == null || bloc.getNomBloc().trim().isEmpty()) {
            log.error("Invalid bloc data: {}", bloc);
            throw new IllegalArgumentException("Bloc name cannot be null or empty");
        }
        if (bloc.getFoyerId() == null || bloc.getFoyerId() <= 0) {
            log.error("Invalid foyer ID for bloc: {}", bloc.getFoyerId());
            throw new IllegalArgumentException("Invalid foyer ID: " + bloc.getFoyerId());
        }
        // Validate Foyer existence
        try {
            Foyer foyer = foyerClient.getFoyerById(bloc.getFoyerId());
            if (foyer == null) {
                log.error("Foyer with ID {} does not exist", bloc.getFoyerId());
                throw new EntityNotFoundException("Foyer not found with id: " + bloc.getFoyerId());
            }
        } catch (Exception e) {
            log.warn("Failed to validate Foyer with ID {}: {}. Proceeding with assignment.", bloc.getFoyerId(), e.getMessage());
        }
        log.info("Adding bloc {} and assigning to foyer {}", bloc.getNomBloc(), bloc.getFoyerId());
        bloc = blocRepository.save(bloc);
        // Notify Foyer service
        AssignBlocToFoyerRequest request = new AssignBlocToFoyerRequest();
        request.setIdBloc(bloc.getIdBloc());
        request.setIdFoyer(bloc.getFoyerId());
        try {
            foyerClient.assignBlocToFoyer(request);
        } catch (Exception e) {
            log.error("Failed to notify Foyer service for bloc {} and foyer {}: {}", bloc.getIdBloc(), bloc.getFoyerId(), e.getMessage());
        }
        return bloc;
    }

    @Override
    @Transactional
    public Bloc assignBlocToFoyer(Long idFoyer, Long idBloc) {
        if (idBloc == null || idBloc <= 0 || idFoyer == null || idFoyer <= 0) {
            log.error("Invalid IDs - blocId: {}, foyerId: {}", idBloc, idFoyer);
            throw new IllegalArgumentException("Invalid bloc ID or foyer ID");
        }
        // Validate Foyer existence
        try {
            Foyer foyer = foyerClient.getFoyerById(idFoyer);
            if (foyer == null) {
                log.error("Foyer with ID {} does not exist", idFoyer);
                throw new EntityNotFoundException("Foyer not found with id: " + idFoyer);
            }
        } catch (Exception e) {
            log.warn("Failed to validate Foyer with ID {}: {}. Proceeding with assignment.", idFoyer, e.getMessage());
        }
        Bloc bloc = blocRepository.findById(idBloc)
                .orElseThrow(() -> new EntityNotFoundException("Bloc not found with id: " + idBloc));
        log.info("Assigning bloc {} to foyer {}", bloc.getNomBloc(), idFoyer);
        bloc.setFoyerId(idFoyer);
        bloc = blocRepository.save(bloc);
        // Notify Foyer service
        AssignBlocToFoyerRequest request = new AssignBlocToFoyerRequest();
        request.setIdBloc(idBloc);
        request.setIdFoyer(idFoyer);
        try {
            foyerClient.assignBlocToFoyer(request);
        } catch (Exception e) {
            log.error("Failed to notify Foyer service for bloc {} and foyer {}: {}", idBloc, idFoyer, e.getMessage());
        }
        return bloc;
    }

    @Override
    @Transactional
    public Bloc unassignBlocFromFoyer(Long idBloc) {
        if (idBloc == null || idBloc <= 0) {
            log.error("Invalid bloc ID for unassignment: {}", idBloc);
            throw new IllegalArgumentException("Invalid bloc ID: " + idBloc);
        }
        Bloc bloc = blocRepository.findById(idBloc)
                .orElseThrow(() -> new EntityNotFoundException("Bloc not found with id: " + idBloc));
        Long foyerId = bloc.getFoyerId();
        log.info("Unassigning bloc {} from foyer {}", bloc.getNomBloc(), foyerId);
        bloc.setFoyerId(null);
        bloc = blocRepository.save(bloc);
        // Notify Foyer service
        if (foyerId != null) {
            AssignBlocToFoyerRequest request = new AssignBlocToFoyerRequest();
            request.setIdBloc(idBloc);
            request.setIdFoyer(foyerId);
            try {
                foyerClient.unassignBlocFromFoyer(request);
            } catch (Exception e) {
                log.error("Failed to notify Foyer service for unassignment of bloc {} from foyer {}: {}", idBloc, foyerId, e.getMessage());
            }
        }
        return bloc;
    }

    @Override
    public List<Bloc> getBlocsByFoyerId(Long foyerId) {
        if (foyerId == null || foyerId <= 0) {
            log.error("Invalid foyer ID for fetching blocs: {}", foyerId);
            throw new IllegalArgumentException("Invalid foyer ID: " + foyerId);
        }
        log.info("Fetching blocs for foyer {}", foyerId);
        return blocRepository.findByFoyerId(foyerId);
    }
}