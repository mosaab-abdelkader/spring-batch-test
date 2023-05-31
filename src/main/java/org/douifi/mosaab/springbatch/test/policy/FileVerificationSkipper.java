package fr.sfr.sumo.xms.srr.alim.policy;

import fr.sfr.sumo.xms.srr.alim.exception.*;
import fr.sfr.sumo.xms.srr.alim.model.SuiviAlimentation;
import fr.sfr.sumo.xms.srr.alim.model.SuiviKafkaNotification;
import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviAlimentationRepository;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviKafkaNotifRepository;
import fr.sfr.sumo.xms.srr.alim.service.MetricServiceTicketsRejetes;
import fr.sfr.sumo.xms.srr.alim.utils.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.FileNotFoundException;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Optional;


public class FileVerificationSkipper implements SkipPolicy {
	@Autowired
	MetricServiceTicketsRejetes metricServiceTicketsRejetes;
	@Autowired
	SuiviKafkaNotifRepository suiviKafkaNotifRepository;
	@Autowired
	SuiviAlimentationRepository suiviAlimentationRepository;
	@Value("${sumo.collecte}")
	String COLLECTE;
	@Value("${sumo.operateur}")
	String OPERATEUR;
	@Value("${file_name}")
	String FILE_NAME;
	@Value("${id_trtm}")
	long ID_TRTM;
	@Value("${directory.tmp.path}")
	String TMP_DIR;
	@Value("${numr_fich}")
	String NUMR_FICH;
	

	private static final int NBCOL=81;
	private static final int ABST =82;
	 
	 
	private static final Logger logger = LoggerFactory.getLogger(FileVerificationSkipper.class);

	@Override
	public boolean shouldSkip(@NotNull Throwable exception, int skipCount) throws SkipLimitExceededException {

		if (exception instanceof FileNotFoundException)
			return false;

		else if (exception instanceof FlatFileParseException ffpe) {
			String errorMessage = "An error occured while processing the " +
				ffpe.getLineNumber() +
				" line of the file. Below was the faulty input.\n" +
				ffpe.getInput() + "\n";
			logger.error("{}", errorMessage);

			if (!ffpe.getInput().startsWith("T"))
				handleTicketsRejetesCase(ffpe.getInput(), NBCOL);
			return true;

		} else if (exception instanceof FieldMissingException fme) {
			logger.error("Error Missing Field --------------- ****** {}", exception.toString());
             
			XmsSrr xmssrr=fme.getXmsSrr(); 			
			String xmsSrrMissingField = xmssrr.toCsv();
			if (!xmsSrrMissingField.startsWith("T"))
				handleTicketsRejetesCase(xmsSrrMissingField, ABST);

			return true;

		} else if(exception instanceof FieldNotNumericException ) {


			logger.error("Filed not numeric Exception --------------- ****** {}", exception.toString());
			return true;
		}
		else if (exception instanceof org.hibernate.exception.DataException) {
			logger.error("Error Exception --------------- ****** {}", exception.toString());
			return true;

		} else if (exception instanceof TicketValidationException) {
			logger.error("Ticket en Exception --------------- ****** {}", exception.toString());
			return true;

		} else if (exception instanceof TicketDateParseException) {
			logger.error("Ticket date parse exception --------------- ****** {}", exception.toString());
			return true;

		} else if (exception instanceof ChecksumAlreadyExistsException) {
			return false;
		}
		else {
			
			logger.error("Skip count --------------- ****** {}", skipCount);
			
			 exception.printStackTrace();
			
			return false;
		}
	}

	private void handleTicketsRejetesCase(String input, int errorCode) {
		
		FileUtils.writeRejectedTicket(TMP_DIR + FILE_NAME, input , errorCode,Integer.parseInt(NUMR_FICH));
		Optional<SuiviKafkaNotification> suiviKafkaNotificationOptional = suiviKafkaNotifRepository.findByCollecteFlux(COLLECTE, OPERATEUR);

		suiviKafkaNotificationOptional.ifPresentOrElse(suiviKafkaNotification -> {
			long nbTicketReject = suiviKafkaNotification.getNbTicketReject();		
			nbTicketReject++;
			metricServiceTicketsRejetes.inc(nbTicketReject);
			try {
				metricServiceTicketsRejetes.pushMetrics();

			} catch (MetricException e) {
				logger.warn("FAILED to  push metrics {}", LocalTime.now());
				
			}
			suiviKafkaNotification.setNbTicketReject(nbTicketReject);
			suiviKafkaNotification.setLastUpdate(Calendar.getInstance().getTime());
			suiviKafkaNotifRepository.save(suiviKafkaNotification);
		}, () -> logger.warn("No SuiviKafkaNotification with Collecte : " + COLLECTE + " and Operateur : " + OPERATEUR + " was found"));

		Optional<SuiviAlimentation> suiviAlimentationOptional = suiviAlimentationRepository.findByIdTrtmAndNomFich(ID_TRTM, FILE_NAME);

		suiviAlimentationOptional.ifPresentOrElse(suiviAlimentation -> {
			int nmbRejt = suiviAlimentation.getNombLignRejt();
			nmbRejt++;
			suiviAlimentation.setNombLignRejt(nmbRejt);
			suiviAlimentationRepository.save(suiviAlimentation);
		}, () -> logger.warn("No SuiviAlimentation with idTrtm : " + ID_TRTM + " and nomFich : " + FILE_NAME + " was found"));
	}


}


