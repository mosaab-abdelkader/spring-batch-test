package fr.sfr.sumo.xms.srr.alim.exception;

public class FluxFromDBException extends Exception {
    public FluxFromDBException(String message, Exception e) {
        super(message, e);
    }
}