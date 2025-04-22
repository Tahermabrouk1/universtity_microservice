package tn.esprit.tpfoyer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.entity.Etudiant;
import tn.esprit.tpfoyer.service.IEtudiantService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/etudiant")
@Tag(name = "Gestion Etudiants")
public class EtudiantRestController {
    IEtudiantService etudiantService;

    @Operation(description = "récupérer toutes les étudiants de la base de données")
    @GetMapping("retrieve-all-etudiants")
    public List<Etudiant> getAllEtudiants(){
        return etudiantService.retrieveAllEtudiants();
    }

    @Operation(description = "récupérer une étudiant par id")
    @GetMapping("retreive-etudiant/{etudiant-id}")
    public Etudiant getEtudiant(@PathVariable("etudiant-id") Long etudiantId) {
        return etudiantService.retrieveEtudiant(etudiantId);
    }

    @Operation(description = "Ajouter une étudiant dans la base de données")
    @PostMapping("add-etudiant")
    public Etudiant addEtudiant(@RequestBody Etudiant etudiant){
        return etudiantService.addEtudiant(etudiant);
    }
    @Operation(description = "retirer une étudiant par id")
    @DeleteMapping("remove-etudiant/{etudiant-id}")
    public void removeEtudiant(@PathVariable("etudiant-id") Long etudiantId){
        etudiantService.removeEtudiant(etudiantId);
    }
    @Operation(description = "Misé à jour d'une étudiant de la base de données")
    @PutMapping("modify-etudiant")
    public Etudiant modifyEtudiant(@RequestBody Etudiant etudiant){
        return etudiantService.modifyEtudiant(etudiant);
    }
}
