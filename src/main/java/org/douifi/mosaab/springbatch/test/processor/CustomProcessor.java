package fr.sfr.sumo.xms.srr.alim.processor;

import fr.sfr.sumo.xms.srr.alim.exception.TicketDateParseException;
import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomProcessor implements ItemProcessor<XmsSrr,XmsSrr> {
    @Value("${numr_fich}")
    String numrFich;
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomProcessor.class);

    public static LocalDateTime TICKET_PLUS_RECENT = LocalDateTime.MIN;
    public static LocalDateTime TICKET_PLUS_VIEUX = LocalDateTime.MAX;
    @Override
    public XmsSrr process(XmsSrr xmsSrr) throws TicketDateParseException {
        xmsSrr.setNUMR_FICH(numrFich);
        xmsSrr.setCOLLECTE("XMS");
        xmsSrr.setOPERATEUR("SRR");
        LOGGER.info(xmsSrr.getDAT_DEBT_COMM());
        LOGGER.info(xmsSrr.getHEUR_DEBT_COMM());
        String[] dateSplit = xmsSrr.getDAT_DEBT_COMM().split("(?<=\\G..)");
        xmsSrr.setDAT_DEBT_COMM(dateSplit[0]+dateSplit[1]+"-"+dateSplit[2]+"-"+dateSplit[3]);
        String[] heureSplit = xmsSrr.getHEUR_DEBT_COMM().split("(?<=\\G..)");
        xmsSrr.setHEUR_DEBT_COMM(heureSplit[0]+":"+heureSplit[1]+":"+heureSplit[2]);

        String unparsedDate = xmsSrr.getDAT_DEBT_COMM()+"-"+ xmsSrr.getHEUR_DEBT_COMM();
        LOGGER.info(unparsedDate);
        DateTimeFormatter formatter ;
        try {
        	
        	 LOGGER.info("Converting date --------------------->{}",unparsedDate);
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss");
        }catch (DateTimeParseException e){
            LOGGER.error("Problem parse date ***");
            throw new TicketDateParseException(xmsSrr, String.format("The format date is not correct"));
        }

        LocalDateTime datExtrFich = LocalDateTime.parse(unparsedDate, formatter);

        if (datExtrFich.isAfter(TICKET_PLUS_RECENT))
            TICKET_PLUS_RECENT = datExtrFich;

        if (datExtrFich.isBefore(TICKET_PLUS_VIEUX))
            TICKET_PLUS_VIEUX = datExtrFich;

        return xmsSrr;
    }
}
