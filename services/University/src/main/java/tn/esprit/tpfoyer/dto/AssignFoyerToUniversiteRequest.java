package tn.esprit.tpfoyer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignFoyerToUniversiteRequest {
    @NotNull(message = "Universite ID is required")
    private Long universiteId;
    @NotNull(message = "Foyer ID is required")
    private Long foyerId;
}