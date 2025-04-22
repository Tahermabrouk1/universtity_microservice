package tn.esprit.tpfoyer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Etudiant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomEt;
    private String prenomEt;
    private Long cin;
    private String Email;
    private String phone;
    private String ecole;
    private Date dateNaissance;

    @Enumerated(EnumType.STRING)
    private Role typeC;


}
