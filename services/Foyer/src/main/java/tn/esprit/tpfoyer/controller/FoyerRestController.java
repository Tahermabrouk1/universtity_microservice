package tn.esprit.tpfoyer.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.Services.IFoyerService;
import tn.esprit.tpfoyer.dto.AssignBlocToFoyerRequest;
import tn.esprit.tpfoyer.entity.Foyer;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/foyer")
@Tag(name = "Gestion Foyer")
public class FoyerRestController {

    private final IFoyerService foyerService;

    @Operation(summary = "Retrieve all foyers", description = "Fetches a list of all foyers")
    @ApiResponse(responseCode = "200", description = "List of foyers retrieved successfully")
    @GetMapping
    public List<Foyer> getAllFoyers() {
        return foyerService.retrieveAllFoyers();
    }

    @Operation(summary = "Retrieve a foyer by ID", description = "Fetches a foyer by its ID")
    @ApiResponse(responseCode = "200", description = "Foyer retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Foyer not found")
    @GetMapping("/{foyer-id}")
    public Foyer getFoyer(@PathVariable("foyer-id") Long foyerId) {
        return foyerService.retrieveFoyer(foyerId);
    }

    @Operation(summary = "Add a new foyer", description = "Creates a new foyer")
    @ApiResponse(responseCode = "201", description = "Foyer created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid foyer data")
    @PostMapping
    public ResponseEntity<Foyer> addFoyer(@Valid @RequestBody Foyer foyer) {
        Foyer createdFoyer = foyerService.addFoyer(foyer);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFoyer);
    }

    @Operation(summary = "Delete a foyer", description = "Deletes a foyer by its ID")
    @ApiResponse(responseCode = "204", description = "Foyer deleted successfully")
    @ApiResponse(responseCode = "404", description = "Foyer not found")
    @DeleteMapping("/{foyer-id}")
    public ResponseEntity<Void> removeFoyer(@PathVariable("foyer-id") Long foyerId) {
        foyerService.removeFoyer(foyerId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Modify a foyer", description = "Updates an existing foyer")
    @ApiResponse(responseCode = "200", description = "Foyer updated successfully")
    @ApiResponse(responseCode = "404", description = "Foyer not found")
    @ApiResponse(responseCode = "400", description = "Invalid foyer data")
    @PutMapping
    public Foyer modifyFoyer(@Valid @RequestBody Foyer foyer) {
        return foyerService.modifyFoyer(foyer);
    }

    @Operation(summary = "Assign a bloc to a foyer", description = "Assigns a specified bloc to a specified foyer")
    @ApiResponse(responseCode = "201", description = "Bloc assigned successfully")
    @ApiResponse(responseCode = "404", description = "Foyer or Bloc not found")
    @ApiResponse(responseCode = "400", description = "Invalid bloc or foyer ID")
    @PostMapping("/assign-bloc")
    public ResponseEntity<Foyer> affectBlocToFoyer(@Valid @RequestBody AssignBlocToFoyerRequest request) {
        Foyer foyer = foyerService.affectBlocToFoyer(request.getIdBloc(), request.getIdFoyer());
        return ResponseEntity.status(HttpStatus.CREATED).body(foyer);
    }

    @Operation(summary = "Unassign a bloc from a foyer", description = "Removes a bloc from a foyer")
    @ApiResponse(responseCode = "200", description = "Bloc unassigned successfully")
    @ApiResponse(responseCode = "404", description = "Foyer or Bloc not found")
    @ApiResponse(responseCode = "400", description = "Invalid bloc or foyer ID")
    @PostMapping("/unassign-bloc")
    public ResponseEntity<Foyer> desaffectBlocFromFoyer(@Valid @RequestBody AssignBlocToFoyerRequest request) {
        Foyer foyer = foyerService.desaffectBlocFromFoyer(request.getIdBloc(), request.getIdFoyer());
        return ResponseEntity.ok(foyer);
    }

    @Operation(summary = "Assign a university to a foyer", description = "Assigns a university to a foyer")
    @ApiResponse(responseCode = "200", description = "University assigned successfully")
    @ApiResponse(responseCode = "404", description = "Foyer not found")
    @ApiResponse(responseCode = "400", description = "Invalid foyer or university ID")
    @PutMapping("/{foyer-id}/assign-universite/{universite-id}")
    public Foyer assignUniversiteToFoyer(@PathVariable("foyer-id") Long foyerId, @PathVariable("universite-id") Long universiteId) {
        return foyerService.assignUniversiteToFoyer(foyerId, universiteId);
    }

    @Operation(summary = "Unassign a university from a foyer", description = "Removes a university from a foyer")
    @ApiResponse(responseCode = "200", description = "University unassigned successfully")
    @ApiResponse(responseCode = "404", description = "Foyer not found")
    @ApiResponse(responseCode = "400", description = "Invalid foyer ID")
    @PutMapping("/{foyer-id}/unassign-universite")
    public Foyer unassignUniversiteFromFoyer(@PathVariable("foyer-id") Long foyerId) {
        return foyerService.unassignUniversiteFromFoyer(foyerId);
    }
}