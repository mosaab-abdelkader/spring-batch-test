package fr.sfr.sumo.xms.srr.alim.exception;

import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;

public class FieldMissingException extends Exception {

    private final XmsSrr xmsSrr;

    public FieldMissingException(XmsSrr xmsSrr, String message) {
        super(message);
        this.xmsSrr = xmsSrr;
    }

    public XmsSrr getXmsSrr() {
        return xmsSrr;
    }
}