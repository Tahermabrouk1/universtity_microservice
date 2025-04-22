package tn.esprit.tpfoyer.control;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.dto.AssignBlocToFoyerRequest;
import tn.esprit.tpfoyer.entity.Bloc;
import tn.esprit.tpfoyer.service.IBlocService;

import java.util.List;

@RestController
@AllArgsConstructor
@Tag(name = "Gestion Bloc")
@RequestMapping("/bloc")
public class BlocRestController {

    private IBlocService blocService;

    @Operation(description = "Récupérer toutes les blocs")
    @GetMapping
    public List<Bloc> getAllBlocs() {
        return blocService.retrieveAllBlocs();
    }

    @Operation(description = "Récupérer un bloc par ID")
    @GetMapping("/{bloc-id}")
    public Bloc getBlocById(@PathVariable("bloc-id") Long blocId) {
        return blocService.retrieveBloc(blocId);
    }

    @Operation(description = "Ajouter un bloc")
    @PostMapping
    public Bloc addBloc(@RequestBody Bloc bloc) {
        return blocService.addBloc(bloc);
    }

    @Operation(description = "Supprimer un bloc par ID")
    @DeleteMapping("/{bloc-id}")
    public void removeBloc(@PathVariable("bloc-id") Long blocId) {
        blocService.removeBloc(blocId);
    }

    @Operation(description = "Modifier un bloc")
    @PutMapping
    public Bloc modifyBloc(@RequestBody Bloc bloc) {
        return blocService.modifyBloc(bloc);
    }

    @Operation(description = "Ajouter un bloc et l'assigner à un foyer")
    @PostMapping("/add-and-assign")
    public Bloc addBlocAndAssignToFoyer(@RequestBody Bloc bloc) {
        return blocService.addBlocAndAssignToFoyer(bloc);
    }

    @Operation(description = "Assigner un bloc à un foyer")
    @PostMapping("/assign-to-foyer")
    public ResponseEntity<Bloc> assignBlocToFoyer(@RequestBody AssignBlocToFoyerRequest request) {
        Bloc bloc = blocService.assignBlocToFoyer(request.getIdFoyer(), request.getIdBloc());
        return ResponseEntity.ok(bloc);
    }

    @Operation(description = "Désassigner un bloc d'un foyer")
    @PostMapping("/unassign")
    public ResponseEntity<Bloc> unassignBlocFromFoyer(@RequestBody AssignBlocToFoyerRequest request) {
        Bloc bloc = blocService.unassignBlocFromFoyer(request.getIdBloc());
        return ResponseEntity.ok(bloc);
    }

    @Operation(description = "Récupérer les blocs par foyer ID")
    @GetMapping("/by-foyer/{foyer-id}")
    public List<Bloc> getBlocsByFoyerId(@PathVariable("foyer-id") Long foyerId) {
        return blocService.getBlocsByFoyerId(foyerId);
    }
}