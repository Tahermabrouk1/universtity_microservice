package tn.esprit.tpfoyer.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.entity.Chambre;
import tn.esprit.tpfoyer.entity.TypeChambre;
import tn.esprit.tpfoyer.service.IChambreService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/chambre")
@Tag(name = "Gestion Chambre")
public class ChambreRestController {
    IChambreService chambreService;

    // http://localhost:8089/tpfoyer/chambre/retrieve-all-chambres
    @Operation(description = "récupérer toutes les chambres de la base de données")
    @GetMapping("/retrieve-all-chambres")
    public List<Chambre> getChambres() {
        List<Chambre> listChambres = chambreService.retrieveAllChambres();
        return listChambres;
    }
    // http://localhost:8089/tpfoyer/chambre/retrieve-chambre/8
    @GetMapping("/retrieve-chambre/{chambre-id}")
    @Operation(description = "récupérer la chambre par id ")
    public Chambre retrieveChambre(@PathVariable("chambre-id") Long chId) {
        Chambre chambre = chambreService.retrieveChambre(chId);
        return chambre;
    }
    // http://localhost:8089/tpfoyer/chambre/add-chambre
    @PostMapping("/add-chambre")
    @Operation(description = "Ajouter une chambre dans la base de données")
    public Chambre addChambre(@RequestBody Chambre c) {
        Chambre chambre = chambreService.addChambre(c);
        return chambre;
    }
    // http://localhost:8089/tpfoyer/chambre/remove-chambre/{chambre-id}
    @DeleteMapping("/remove-chambre/{chambre-id}")
    @Operation(description = "retirer une chambre par id")
    public void removeChambre(@PathVariable("chambre-id") Long chId) {

        chambreService.removeChambre(chId);
    }
    // http://localhost:8089/tpfoyer/chambre/modify-chambre
    @PutMapping("/modify-chambre")
    @Operation(description = "mise à jour une chambre de la base de données")
    public Chambre modifyChambre(@RequestBody Chambre c) {
        Chambre chambre = chambreService.modifyChambre(c);
        return chambre;
    }

    @GetMapping("/getChambreByTypeChambre/{chambreType}")
    public List<Chambre> getChambresByTypeChambre(@PathVariable("chambreType") TypeChambre typeC) {
        return chambreService.retriveAllChambresByTypeChambre(typeC);
    }

    @GetMapping("/getChambreByNum/{chambreNum}")
    public Chambre getChambresByTypeChambre(@PathVariable("chambreNum") Long numC) {
        return chambreService.retrieveChambreByNumberChambre(numC);
    }
    @GetMapping("/getChambreByEtudiantcin/{cinE}")
    public Chambre getChambresByCinEtudiant(@PathVariable("cinE") Long cin) {
        return chambreService.retrieveChambreByEtudiantCin(cin);
    }

}
