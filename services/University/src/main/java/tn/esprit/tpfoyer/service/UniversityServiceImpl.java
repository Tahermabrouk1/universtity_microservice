package tn.esprit.tpfoyer.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.tpfoyer.dto.Foyer;
import tn.esprit.tpfoyer.entity.Universite;
import tn.esprit.tpfoyer.exception.FoyerNotFoundException;
import tn.esprit.tpfoyer.exception.UniversiteNotFoundException;
import tn.esprit.tpfoyer.feign.FoyerClient;
import tn.esprit.tpfoyer.repository.UniversiteRepository;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UniversityServiceImpl implements IUniversiteService {

    private final UniversiteRepository universiteRepository;
    private final FoyerClient foyerClient;

    @Override
    public List<Universite> retreiveAllUniversities() {
        log.info("Retrieving all universities");
        return universiteRepository.findAll();
    }

    @Override
    public Universite retrieveUniversity(Long universityId) {
        if (universityId == null || universityId <= 0) {
            log.error("Invalid university ID: {}", universityId);
            throw new IllegalArgumentException("Invalid university ID: " + universityId);
        }
        log.info("Retrieving university with ID: {}", universityId);
        return universiteRepository.findById(universityId)
                .orElseThrow(() -> new UniversiteNotFoundException(universityId));
    }

    @Override
    public Universite addUniversity(Universite universite) {
        validateUniversite(universite);
        log.info("Adding university: {}", universite.getNomUniversite());
        return universiteRepository.save(universite);
    }

    @Override
    @Transactional
    public void removeUniversity(Long universityId) {
        if (universityId == null || universityId <= 0) {
            log.error("Invalid university ID: {}", universityId);
            throw new IllegalArgumentException("Invalid university ID: " + universityId);
        }
        Universite universite = universiteRepository.findById(universityId)
                .orElseThrow(() -> new UniversiteNotFoundException(universityId));
        if (universite.getFoyerId() != null) {
            try {
                foyerClient.unassignUniversiteFromFoyer(universite.getFoyerId());
                log.info("Unassigned foyer {} from university {}", universite.getFoyerId(), universite.getNomUniversite());
            } catch (Exception e) {
                log.error("Failed to unassign Foyer {} from Universite {}: {}", universite.getFoyerId(), universityId, e.getMessage());
                throw new RuntimeException("Failed to unassign Foyer: " + e.getMessage(), e);
            }
        }
        log.info("Removing university: {}", universite.getNomUniversite());
        universiteRepository.deleteById(universityId);
    }

    @Override
    public Universite modifyUniversity(Universite universite) {
        if (universite.getIdUniversite() == null || !universiteRepository.existsById(universite.getIdUniversite())) {
            log.error("University with ID {} not found for modification", universite.getIdUniversite());
            throw new UniversiteNotFoundException(universite.getIdUniversite());
        }
        validateUniversite(universite);
        log.info("Modifying university: {}", universite.getNomUniversite());
        return universiteRepository.save(universite);
    }

    @Override
    @Transactional
    public Universite affectFoyerToUniversite(Long universiteId, Long foyerId) {
        if (universiteId == null || universiteId <= 0 || foyerId == null || foyerId <= 0) {
            log.error("Invalid universite ID {} or foyer ID {}", universiteId, foyerId);
            throw new IllegalArgumentException("Invalid universite ID or foyer ID");
        }
        Universite universite = universiteRepository.findById(universiteId)
                .orElseThrow(() -> new UniversiteNotFoundException(universiteId));

        log.info("Validating Foyer with id {} for Universite {}", foyerId, universite.getNomUniversite());
        Foyer foyer = foyerClient.getFoyerById(foyerId);
        if (foyer == null) {
            throw new FoyerNotFoundException(foyerId);
        }

        if (foyer.getUniversiteId() != null && !foyer.getUniversiteId().equals(universiteId)) {
            log.warn("Foyer {} is already assigned to Universite {}", foyerId, foyer.getUniversiteId());
            throw new IllegalStateException("Foyer " + foyerId + " is already assigned to Universite " + foyer.getUniversiteId());
        }

        universite.setFoyerId(foyerId);
        universiteRepository.save(universite);

        try {
            foyerClient.assignUniversiteToFoyer(foyerId, universiteId);
            log.info("Assigned Foyer {} to Universite {}", foyerId, universite.getNomUniversite());
        } catch (Exception e) {
            log.error("Failed to assign Universite {} to Foyer {}: {}", universiteId, foyerId, e.getMessage());
            universite.setFoyerId(null);
            universiteRepository.save(universite);
            throw new RuntimeException("Failed to update Foyer: " + e.getMessage(), e);
        }

        return universite;
    }

    @Override
    @Transactional
    public Universite desaffectFoyerFromUniversite(Long universiteId) {
        if (universiteId == null || universiteId <= 0) {
            log.error("Invalid university ID: {}", universiteId);
            throw new IllegalArgumentException("Invalid university ID: " + universiteId);
        }
        Universite universite = universiteRepository.findById(universiteId)
                .orElseThrow(() -> new UniversiteNotFoundException(universiteId));

        if (universite.getFoyerId() == null) {
            log.warn("Universite {} is not assigned to any Foyer", universite.getNomUniversite());
            return universite;
        }

        Long foyerId = universite.getFoyerId();
        universite.setFoyerId(null);
        universiteRepository.save(universite);

        try {
            foyerClient.unassignUniversiteFromFoyer(foyerId);
            log.info("Unassigned Foyer {} from Universite {}", foyerId, universite.getNomUniversite());
        } catch (Exception e) {
            log.error("Failed to unassign Foyer {} from Universite {}: {}", foyerId, universiteId, e.getMessage());
            throw new RuntimeException("Failed to update Foyer: " + e.getMessage(), e);
        }

        return universite;
    }

    private void validateUniversite(Universite universite) {
        if (universite == null) {
            throw new IllegalArgumentException("University cannot be null");
        }
        if (universite.getNomUniversite() == null || universite.getNomUniversite().isEmpty()) {
            throw new IllegalArgumentException("University name cannot be null or empty");
        }
        if (universite.getAdresse() == null || universite.getAdresse().isEmpty()) {
            throw new IllegalArgumentException("University address cannot be null or empty");
        }
    }
}