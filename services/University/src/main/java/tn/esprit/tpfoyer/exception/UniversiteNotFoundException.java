package tn.esprit.tpfoyer.exception;

public class UniversiteNotFoundException extends RuntimeException {

    public UniversiteNotFoundException(Long universityId) {
        super("University with ID " + universityId + " not found");
    }

}