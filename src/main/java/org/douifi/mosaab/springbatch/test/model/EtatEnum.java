package fr.sfr.sumo.xms.srr.alim.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EtatEnum {
    ETAT_NOTF("NOTF"),
    ETAT_RECU("RECU"),
    ETAT_CTRL("CTRL"),
    ETAT_CHRG("CHRG"),
    ETAT_REJT("REJT"),
    ETAT_ANNL("ANNL");

    private final String etat;
}
