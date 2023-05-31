package fr.sfr.sumo.xms.srr.alim.exception;

/**
 * SFR
 * 
 * KafkaException class 
 * 
 * 
 * @author zkhiari
 * @version 1.0
 * @since 2023-02-16
 *
 */
public class KafkaException extends Exception{

    public KafkaException(String message, Exception e) {
        super(message, e);
    }
}
