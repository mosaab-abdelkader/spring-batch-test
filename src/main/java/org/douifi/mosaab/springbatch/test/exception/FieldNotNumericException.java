package fr.sfr.sumo.xms.srr.alim.exception;

import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;

public class FieldNotNumericException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 198844412344L;
	private final XmsSrr xmsSrr;

    public FieldNotNumericException(XmsSrr xmsSrr, String message) {
        super(message);
        this.xmsSrr = xmsSrr;
    }

    public XmsSrr getXmsSrr() {
        return xmsSrr;
    }
}