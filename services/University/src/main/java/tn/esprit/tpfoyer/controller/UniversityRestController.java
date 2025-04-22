package tn.esprit.tpfoyer.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.dto.AssignFoyerToUniversiteRequest;
import tn.esprit.tpfoyer.entity.Universite;
import tn.esprit.tpfoyer.service.IUniversiteService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/university")
@Tag(name = "Gestion université")
public class UniversityRestController {

    private final IUniversiteService universiteService;

    @Operation(summary = "Retrieve all universities", description = "Fetches a list of all universities")
    @ApiResponse(responseCode = "200", description = "List of universities retrieved successfully")
    @GetMapping
    public List<Universite> getAllUniversities() {
        return universiteService.retreiveAllUniversities();
    }

    @Operation(summary = "Retrieve a university by ID", description = "Fetches a university by its ID")
    @ApiResponse(responseCode = "200", description = "University retrieved successfully")
    @ApiResponse(responseCode = "404", description = "University not found")
    @GetMapping("/{university-id}")
    public Universite getUniversite(@PathVariable("university-id") Long universityId) {
        return universiteService.retrieveUniversity(universityId);
    }

    @Operation(summary = "Add a new university", description = "Creates a new university")
    @ApiResponse(responseCode = "201", description = "University created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid university data")
    @PostMapping
    public ResponseEntity<Universite> addUniversity(@Valid @RequestBody Universite universite) {
        Universite createdUniversite = universiteService.addUniversity(universite);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUniversite);
    }

    @Operation(summary = "Delete a university", description = "Deletes a university by its ID")
    @ApiResponse(responseCode = "204", description = "University deleted successfully")
    @ApiResponse(responseCode = "404", description = "University not found")
    @DeleteMapping("/{university-id}")
    public ResponseEntity<Void> removeUniversity(@PathVariable("university-id") Long universityId) {
        universiteService.removeUniversity(universityId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Modify a university", description = "Updates an existing university")
    @ApiResponse(responseCode = "200", description = "University updated successfully")
    @ApiResponse(responseCode = "404", description = "University not found")
    @ApiResponse(responseCode = "400", description = "Invalid university data")
    @PutMapping
    public Universite modifyUniversity(@Valid @RequestBody Universite universite) {
        return universiteService.modifyUniversity(universite);
    }

    @Operation(summary = "Assign a foyer to a university", description = "Assigns a specified foyer to a specified university")
    @ApiResponse(responseCode = "201", description = "Foyer assigned successfully")
    @ApiResponse(responseCode = "404", description = "University or Foyer not found")
    @ApiResponse(responseCode = "400", description = "Invalid university or foyer ID")
    @PostMapping("/assign-foyer")
    public ResponseEntity<Universite> assignFoyerToUniversite(@Valid @RequestBody AssignFoyerToUniversiteRequest request) {
        Universite universite = universiteService.affectFoyerToUniversite(request.getUniversiteId(), request.getFoyerId());
        return ResponseEntity.status(HttpStatus.CREATED).body(universite);
    }

    @Operation(summary = "Unassign a foyer from a university", description = "Removes a foyer from a university")
    @ApiResponse(responseCode = "200", description = "Foyer unassigned successfully")
    @ApiResponse(responseCode = "404", description = "University not found")
    @ApiResponse(responseCode = "400", description = "Invalid university ID")
    @PostMapping("/unassign-foyer/{university-id}")
    public ResponseEntity<Universite> unassignFoyerFromUniversite(@PathVariable("university-id") Long universityId) {
        Universite universite = universiteService.desaffectFoyerFromUniversite(universityId);
        return ResponseEntity.ok(universite);
    }
}