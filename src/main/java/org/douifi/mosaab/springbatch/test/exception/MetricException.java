package fr.sfr.sumo.xms.srr.alim.exception;

public class MetricException extends Exception {
    public MetricException(String message, Exception e) {
        super(message, e);
    }
}