package tn.esprit.tpfoyer.dto;

import lombok.Data;

@Data
public class Foyer {
    private Long idFoyer;
    private String nomFoyer;
    private Long capaciteFoyer;
    private Long universiteId;
}