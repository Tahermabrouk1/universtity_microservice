package tn.esprit.tpfoyer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignBlocToFoyerRequest {
    @NotNull(message = "Bloc ID is required")
    private Long idBloc;
    @NotNull(message = "Foyer ID is required")
    private Long idFoyer;
}