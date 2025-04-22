package tn.esprit.tpfoyer.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import tn.esprit.tpfoyer.dto.Foyer;

@FeignClient(name = "foyer-service")
public interface FoyerClient {
    @GetMapping("/foyer/{foyer-id}")
    Foyer getFoyerById(@PathVariable("foyer-id") Long foyerId);

    @PutMapping("/foyer/{foyer-id}/assign-universite/{universite-id}")
    Foyer assignUniversiteToFoyer(@PathVariable("foyer-id") Long foyerId, @PathVariable("universite-id") Long universiteId);

    @PutMapping("/foyer/{foyer-id}/unassign-universite")
    Foyer unassignUniversiteFromFoyer(@PathVariable("foyer-id") Long foyerId);
}