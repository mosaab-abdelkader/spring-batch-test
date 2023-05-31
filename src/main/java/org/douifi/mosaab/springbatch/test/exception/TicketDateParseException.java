package fr.sfr.sumo.xms.srr.alim.exception;

import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;

public class TicketDateParseException extends Exception {

    private final XmsSrr xmsSrr;

    public TicketDateParseException(XmsSrr xmsSrr, String message) {
        super(message);
        this.xmsSrr = xmsSrr;
    }

    public XmsSrr getXmsSrr() {
        return xmsSrr;
    }
}