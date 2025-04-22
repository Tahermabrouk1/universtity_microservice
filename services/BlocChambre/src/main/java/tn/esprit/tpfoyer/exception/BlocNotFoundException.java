package tn.esprit.tpfoyer.exception;

public class BlocNotFoundException extends RuntimeException {

    public BlocNotFoundException(Long idBloc) {
        super("Bloc with ID " + idBloc + " not found");
    }

}