package fr.sfr.sumo.xms.srr.alim.exception;

public class S3ConnectionException extends RuntimeException {
    public S3ConnectionException(String message, Exception e) {
        super(message, e);
    }
}