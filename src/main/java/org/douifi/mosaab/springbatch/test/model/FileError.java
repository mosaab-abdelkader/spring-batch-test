package fr.sfr.sumo.xms.srr.alim.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileError {
	
	FOOTER_IS_INVALID(52, "Pied de page invalide",".PIED_INVALIDE"),
	DOUBLE_FILE_RECEIVED(53, "Fichier reçu en doublon",".FICHIER_DOUBLON"),
    CONTENT_ALREADY_EXISTS(54, "Contenu du fichier reçu en doublon",".TICKETS_DOUBLON");	

	private final int code;
	private final String message;
	private final String suffix;

	
	
	
	
	


}
