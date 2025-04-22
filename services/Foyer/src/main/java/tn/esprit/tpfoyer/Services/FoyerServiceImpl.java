package tn.esprit.tpfoyer.Services;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpfoyer.Feign.BlocFoyer;
import tn.esprit.tpfoyer.Repository.FoyerRepository;
import tn.esprit.tpfoyer.dto.AssignBlocToFoyerRequest;
import tn.esprit.tpfoyer.dto.Bloc;
import tn.esprit.tpfoyer.entity.Foyer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class FoyerServiceImpl implements IFoyerService {
    private final FoyerRepository foyerRepository;
    private final BlocFoyer blocClient;

    @Override
    public List<Foyer> retrieveAllFoyers() {
        log.info("Retrieving all foyers");
        return foyerRepository.findAll();
    }

    @Override
    public Foyer retrieveFoyer(Long foyerId) {
        if (foyerId == null || foyerId <= 0) {
            log.error("Invalid foyer ID: {}", foyerId);
            throw new IllegalArgumentException("Invalid foyer ID: " + foyerId);
        }
        Foyer foyer = foyerRepository.findById(foyerId)
                .orElseThrow(() -> new EntityNotFoundException("Foyer not found with id: " + foyerId));
        try {
            List<Bloc> blocs;
            log.info("Bloc IDs for Foyer {}: {}", foyerId, foyer.getBlocIds());
            if (!foyer.getBlocIds().isEmpty()) {
                blocs = foyer.getBlocIds().stream()
                        .map(blocId -> {
                            try {
                                log.info("Fetching bloc with ID: {}", blocId);
                                return blocClient.getBlocById(blocId);
                            } catch (Exception e) {
                                log.warn("Failed to fetch Bloc {} for Foyer {}: {}", blocId, foyerId, e.getMessage());
                                return null;
                            }
                        })
                        .filter(bloc -> bloc != null)
                        .collect(Collectors.toList());
            } else {
                log.info("Bloc IDs empty, falling back to getBlocsByFoyerId for Foyer {}", foyerId);
                blocs = getBlocsByFoyerId(foyerId);
            }
            foyer.setBlocs(blocs);
            log.info("Successfully fetched {} blocs for Foyer {} (name: {})", blocs.size(), foyerId, foyer.getNomFoyer());
        } catch (Exception e) {
            log.warn("Unable to fetch blocs for Foyer {}: {}. Returning foyer without blocs.", foyerId, e.getMessage());
            foyer.setBlocs(new ArrayList<>());
        }
        log.info("Retrieved foyer: {} (ID: {})", foyer.getNomFoyer(), foyerId);
        return foyer;
    }

    @Override
    public Foyer addFoyer(Foyer foyer) {
        if (foyer == null || foyer.getNomFoyer() == null || foyer.getNomFoyer().trim().isEmpty()) {
            log.error("Invalid foyer data: {}", foyer);
            throw new IllegalArgumentException("Foyer name cannot be null or empty");
        }
        log.info("Adding foyer: {}", foyer.getNomFoyer());
        return foyerRepository.save(foyer);
    }

    @Override
    @Transactional
    public void removeFoyer(Long foyerId) {
        if (foyerId == null || foyerId <= 0) {
            log.error("Invalid foyer ID for deletion: {}", foyerId);
            throw new IllegalArgumentException("Invalid foyer ID: " + foyerId);
        }
        if (!foyerRepository.existsById(foyerId)) {
            throw new EntityNotFoundException("Foyer not found with id: " + foyerId);
        }
        try {
            List<Bloc> blocs = getBlocsByFoyerId(foyerId);
            if (!blocs.isEmpty()) {
                for (Bloc bloc : blocs) {
                    AssignBlocToFoyerRequest request = new AssignBlocToFoyerRequest();
                    request.setIdBloc(bloc.getIdBloc());
                    request.setIdFoyer(foyerId);
                    try {
                        blocClient.removeFoyerFromBloc(request);
                    } catch (Exception e) {
                        log.warn("Failed to unassign Bloc {} from Foyer {}: {}", bloc.getIdBloc(), foyerId, e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Error removing Foyer associations from Blocs for Foyer {}: {}. Proceeding with deletion.", foyerId, e.getMessage());
        }
        log.info("Removing foyer with ID: {}", foyerId);
        foyerRepository.deleteById(foyerId);
    }

    @Override
    public Foyer modifyFoyer(Foyer foyer) {
        if (foyer == null || foyer.getIdFoyer() == null || !foyerRepository.existsById(foyer.getIdFoyer())) {
            log.error("Invalid foyer for modification: {}", foyer);
            throw new EntityNotFoundException("Foyer not found with id: " + (foyer != null ? foyer.getIdFoyer() : "null"));
        }
        log.info("Modifying foyer: {}", foyer.getNomFoyer());
        return foyerRepository.save(foyer);
    }

    @Override
    @Transactional
    public Foyer affectBlocToFoyer(Long blocId, Long foyerId) {
        if (blocId == null || blocId <= 0 || foyerId == null || foyerId <= 0) {
            log.error("Invalid IDs - blocId: {}, foyerId: {}", blocId, foyerId);
            throw new IllegalArgumentException("Invalid bloc ID or foyer ID");
        }
        Foyer foyer = foyerRepository.findById(foyerId)
                .orElseThrow(() -> new EntityNotFoundException("Foyer not found with id: " + foyerId));
        // Validate Bloc existence
        try {
            Bloc bloc = blocClient.getBlocById(blocId);
            if (bloc == null) {
                log.error("Bloc with ID {} does not exist", blocId);
                throw new EntityNotFoundException("Bloc not found with id: " + blocId);
            }
            if (bloc.getFoyerId() != null && !bloc.getFoyerId().equals(foyerId)) {
                log.warn("Bloc {} is already assigned to another Foyer {}", blocId, bloc.getFoyerId());
                throw new IllegalStateException("Bloc " + blocId + " is already assigned to Foyer " + bloc.getFoyerId());
            }
        } catch (Exception e) {
            log.warn("Failed to validate Bloc with ID {}: {}. Proceeding with assignment.", blocId, e.getMessage());
        }
        try {
            log.info("Assigning Bloc {} to Foyer {}", blocId, foyer.getNomFoyer());
            foyer.getBlocIds().add(blocId);
            foyerRepository.save(foyer);
            List<Bloc> blocs = getBlocsByFoyerId(foyerId);
            foyer.setBlocs(blocs);
            log.info("Successfully assigned Bloc {} to Foyer {}", blocId, foyer.getNomFoyer());
        } catch (Exception e) {
            log.error("Failed to assign Bloc {} to Foyer {}: {}", blocId, foyer.getNomFoyer(), e.getMessage());
            throw new RuntimeException("Failed to assign Bloc to Foyer: " + e.getMessage(), e);
        }
        return foyer;
    }

    @Override
    @Transactional
    public Foyer desaffectBlocFromFoyer(Long blocId, Long foyerId) {
        if (blocId == null || blocId <= 0 || foyerId == null || foyerId <= 0) {
            log.error("Invalid IDs - blocId: {}, foyerId: {}", blocId, foyerId);
            throw new IllegalArgumentException("Invalid bloc ID or foyer ID");
        }
        Foyer foyer = foyerRepository.findById(foyerId)
                .orElseThrow(() -> new EntityNotFoundException("Foyer not found with id: " + foyerId));
        try {
            log.info("Unassigning Bloc {} from Foyer {}", blocId, foyer.getNomFoyer());
            // Remove blocId and persist
            foyer.getBlocIds().remove(blocId);
            foyerRepository.save(foyer);

            AssignBlocToFoyerRequest request = new AssignBlocToFoyerRequest();
            request.setIdBloc(blocId);
            request.setIdFoyer(foyerId);
            try {
                blocClient.removeFoyerFromBloc(request);
            } catch (Exception e) {
                log.warn("Failed to notify Bloc service for unassignment of Bloc {} from Foyer {}: {}", blocId, foyerId, e.getMessage());
            }
            List<Bloc> blocs = getBlocsByFoyerId(foyerId);
            foyer.setBlocs(blocs);
            log.info("Successfully unassigned Bloc {} from Foyer {}", blocId, foyer.getNomFoyer());
        } catch (Exception e) {
            log.error("Failed to unassign Bloc {} from Foyer {}: {}", blocId, foyer.getNomFoyer(), e.getMessage());
            throw new RuntimeException("Failed to unassign Bloc from Foyer: " + e.getMessage(), e);
        }
        return foyer;
    }

    @Override
    public List<Bloc> getBlocsByFoyerId(Long foyerId) {
        if (foyerId == null || foyerId <= 0) {
            log.error("Invalid foyer ID for fetching blocs: {}", foyerId);
            throw new IllegalArgumentException("Invalid foyer ID: " + foyerId);
        }
        try {
            log.info("Fetching Blocs for Foyer {}", foyerId);
            List<Bloc> blocs = blocClient.getBlocsByFoyerId(foyerId);
            log.info("Fetched {} blocs for Foyer {}", blocs.size(), foyerId);
            return blocs;
        } catch (Exception e) {
            log.error("Failed to fetch Blocs for Foyer {}: {}", foyerId, e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public Foyer assignUniversiteToFoyer(Long foyerId, Long universiteId) {
        if (foyerId == null || foyerId <= 0 || universiteId == null || universiteId <= 0) {
            log.error("Invalid IDs - foyerId: {}, universiteId: {}", foyerId, universiteId);
            throw new IllegalArgumentException("Invalid foyer ID or universite ID");
        }
        Foyer foyer = foyerRepository.findById(foyerId)
                .orElseThrow(() -> new EntityNotFoundException("Foyer not found with id: " + foyerId));

        if (foyer.getUniversiteId() != null && !foyer.getUniversiteId().equals(universiteId)) {
            log.warn("Foyer {} is already assigned to Universite {}", foyer.getNomFoyer(), foyer.getUniversiteId());
            throw new IllegalStateException("Foyer " + foyerId + " is already assigned to Universite " + foyer.getUniversiteId());
        }

        foyer.setUniversiteId(universiteId);
        log.info("Assigned Universite {} to Foyer {}", universiteId, foyer.getNomFoyer());
        return foyerRepository.save(foyer);
    }

    @Override
    @Transactional
    public Foyer unassignUniversiteFromFoyer(Long foyerId) {
        if (foyerId == null || foyerId <= 0) {
            log.error("Invalid foyer ID for unassigning universite: {}", foyerId);
            throw new IllegalArgumentException("Invalid foyer ID: " + foyerId);
        }
        Foyer foyer = foyerRepository.findById(foyerId)
                .orElseThrow(() -> new EntityNotFoundException("Foyer not found with id: " + foyerId));

        if (foyer.getUniversiteId() == null) {
            log.warn("Foyer {} is not assigned to any Universite", foyer.getNomFoyer());
            return foyer;
        }

        Long universiteId = foyer.getUniversiteId();
        foyer.setUniversiteId(null);
        log.info("Unassigned Universite {} from Foyer {}", universiteId, foyer.getNomFoyer());
        return foyerRepository.save(foyer);
    }
}