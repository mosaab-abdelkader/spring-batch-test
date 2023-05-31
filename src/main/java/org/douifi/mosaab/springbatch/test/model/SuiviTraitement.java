package fr.sfr.sumo.xms.srr.alim.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "suivi_traitement")
public class SuiviTraitement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private long id;

    @Column(name = "id_trtm")
    private long idTrtm;

    @Column(name = "nom_trtm", nullable = false)
    private String nomTrtm;

    @Column(name = "nom_etape", nullable = false)
    private String nomEtape;

    @Column(name = "date_debt_trtm", nullable = false)
    private LocalDateTime dateDebtTrtm;

    @Column(name = "date_fin_trtm")
    private LocalDateTime dateFinTrtm;

    @Column(name = "code_retr")
    private int codeRetr;

    @Column(name = "libelle_erreur")
    private String libelleErreur;
}