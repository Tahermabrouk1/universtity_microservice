package tn.esprit.tpfoyer.dto;

import lombok.Data;

@Data
public class Bloc {
    private Long idBloc;
    private String nomBloc;
    private Long capaciteBloc;
    private Long foyerId;
}