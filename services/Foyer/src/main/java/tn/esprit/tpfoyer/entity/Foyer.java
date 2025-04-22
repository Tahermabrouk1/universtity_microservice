package tn.esprit.tpfoyer.entity;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit.tpfoyer.dto.Bloc;
import tn.esprit.tpfoyer.dto.Universite;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Foyer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idFoyer;
    private String nomFoyer;
    private Long capaciteFoyer;
    private Long universiteId;

    @ElementCollection
    private Set<Long> blocIds = new HashSet<>();
    @Transient
    private List<Bloc> blocs = new ArrayList<>();

}