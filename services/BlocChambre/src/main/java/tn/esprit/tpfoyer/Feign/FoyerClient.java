package tn.esprit.tpfoyer.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tn.esprit.tpfoyer.dto.AssignBlocToFoyerRequest;
import tn.esprit.tpfoyer.dto.Foyer;

@FeignClient(name = "foyer-service")
public interface FoyerClient {
    @GetMapping("/foyer/{foyer-id}")
    Foyer getFoyerById(@PathVariable("foyer-id") Long id);
    @PostMapping("/foyer/assign-bloc")
    void assignBlocToFoyer(@RequestBody AssignBlocToFoyerRequest request);
    @PostMapping("/foyer/unassign-bloc")
    void unassignBlocFromFoyer(@RequestBody AssignBlocToFoyerRequest request);
}