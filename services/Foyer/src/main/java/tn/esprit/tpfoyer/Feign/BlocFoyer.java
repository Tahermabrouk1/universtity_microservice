package tn.esprit.tpfoyer.Feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import tn.esprit.tpfoyer.dto.AssignBlocToFoyerRequest;
import tn.esprit.tpfoyer.dto.Bloc;

import java.util.List;

@FeignClient(name = "bloc-service")
public interface BlocFoyer {

    @GetMapping("/bloc/by-foyer/{foyerId}")
    List<Bloc> getBlocsByFoyerId(@PathVariable("foyerId") Long foyerId);

    @GetMapping("/bloc/{blocId}")
    Bloc getBlocById(@PathVariable("blocId") Long blocId);

    @PostMapping("/bloc/assign-to-foyer")
    void assignFoyerToBloc(@RequestBody AssignBlocToFoyerRequest request);

    @PostMapping("/bloc/unassign")
    void removeFoyerFromBloc(@RequestBody AssignBlocToFoyerRequest request);
}