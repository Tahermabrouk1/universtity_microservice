package tn.esprit.tpfoyer.exception;

public class FoyerNotFoundException extends RuntimeException {

    public FoyerNotFoundException(Long idFoyer) {
        super("Foyer with ID " + idFoyer + " not found");
    }

}