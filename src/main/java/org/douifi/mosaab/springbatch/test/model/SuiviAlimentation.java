package fr.sfr.sumo.xms.srr.alim.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "suivi_alimentation")
public class SuiviAlimentation {
    @Id
    @Column(name = "id_trtm", nullable = false)
    private long idTrtm;

    @Column(name = "numr_fich", nullable = false, length = 9)
    private int numrFich;

    @Column(nullable = false)
    private String operateur;

    @Column(nullable = false)
    private String collecte;

    @Column(name = "seqn_fich", nullable = false)
    private int seqnFich;

    @Column(name = "nom_fich", nullable = false)
    private String nomFich;


    @Column(name = "dat_extr_fich")
    private LocalDateTime datExtrFich;

    @Column(name = "date_notif")
    private LocalDateTime dateNotif;

    @Column(name = "date_recp")
    private LocalDateTime dateRecp;

    @Column(name = "dat_debt_chrg")
    private LocalDateTime datDebtChrg;

    @Column(name = "dat_fin_chrg")
    private LocalDateTime datFinChrg;

    @Column(name = "date_fin")
    private LocalDateTime dateFin;

    @Column(name = "chks_fich")
    private String chksFich;

    @Column(name = "nomb_lign_attn")
    private int nombLignAttn;

    @Column(name = "nomb_lign_reel_fich")
    private int nombLignReelFich;

    @Column(name = "nomb_lign_chrg")
    private int nombLignChrg;

    @Column(name = "nomb_lign_rejt_excp")
    private int nombLignRejtExcp;

    @Column(name = "nomb_lign_rejt")
    private int nombLignRejt;

    @Column(name = "dat_annl_fich")
    private LocalDateTime datAnnlFich;

    @Column(name = "etat")
    private String etat;
    
    @Column(name = "statut_sync")
    private String statutSync;
    
    @Column(name = "nomb_lign_chrg_es", nullable = false)
    private long nombLignChrgEs;

    @Column(name = "ticket_plus_recent")
    private LocalDateTime ticketPlusRecent;

    @Column(name = "ticket_plus_vieux")
    private LocalDateTime ticketPlusVieux;
}