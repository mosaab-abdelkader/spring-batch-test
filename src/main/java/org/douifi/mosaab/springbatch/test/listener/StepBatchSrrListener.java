package fr.sfr.sumo.xms.srr.alim.listener;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Optional;
import fr.sfr.sumo.xms.srr.alim.exception.MetricException;
import fr.sfr.sumo.xms.srr.alim.model.SuiviAlimentation;
import fr.sfr.sumo.xms.srr.alim.model.SuiviFichierSumo;
import fr.sfr.sumo.xms.srr.alim.model.SuiviTraitement;
import fr.sfr.sumo.xms.srr.alim.model.SuiviKafkaNotification;
import fr.sfr.sumo.xms.srr.alim.processor.CustomProcessor;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviAlimentationRepository;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviTraitementRepository;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviKafkaNotifRepository;
import fr.sfr.sumo.xms.srr.alim.service.ElasticService;
import fr.sfr.sumo.xms.srr.alim.service.MetricServiceFileCounter;
import fr.sfr.sumo.xms.srr.alim.service.MetricServiceTicketsRead;
import fr.sfr.sumo.xms.srr.alim.service.MetricServiceTicketsWrite;
import fr.sfr.sumo.xms.srr.alim.utils.FileUtils;

import org.jetbrains.annotations.NotNull;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import static fr.sfr.sumo.xms.srr.alim.model.EtatEnum.*;

public class StepBatchSrrListener implements StepExecutionListener {
	private static final Logger LOGGER = LoggerFactory.getLogger(StepBatchSrrListener.class);

	@Value("${sumo.collecte}")
	String collecte;
	@Value("${sumo.operateur}")
	String operateur;
	@Value("${id_trtm}")
	long idTrtm;
	@Value("${file_name}")
	String fileName;
	@Autowired
	SuiviKafkaNotifRepository suiviKafkaNotifRepository;
	@Autowired
	SuiviFichierSumo suiviFichierSumo;
	@Autowired
	SuiviAlimentationRepository suiviAlimentationRepository;
	@Autowired
	SuiviTraitementRepository suiviTraitementRepository;
	@Autowired
	MetricServiceTicketsRead metricServiceTicketsRead;
	@Autowired
	MetricServiceTicketsWrite metricServiceTicketsWrite;
	@Autowired
	ElasticService elasticService;
	@Autowired
	MetricServiceFileCounter metricServiceFileCounter;


	  private static final String INJECTION = "Injection";
	  private static final String INJECTION_ELASTIC = "Injection Elastic";
	  private static final String INJECTION_KAFKA =  "Injection Kafka";
	  private static final String LIB_ERREUR = "Etape Ok";
	
	
	
	@Override
	public void beforeStep(@NotNull StepExecution stepExecution) {

		suiviFichierSumo.setCollecte(collecte);
		suiviFichierSumo.setOperateur(operateur);
		suiviFichierSumo.setStart(Instant.now().toString());
		suiviFichierSumo.setFile(fileName);
		suiviFichierSumo.setCount(FileUtils.lineNumbers);
	}

	@Override
	public ExitStatus afterStep(@NotNull StepExecution stepExecution) {

		feedStepInjectionKafkaAndInjectionElasticSuiviTraitement();
		feedStepFourSuiviAlimentation();


		Optional<SuiviKafkaNotification> suiviKafkaNotificationOptional = suiviKafkaNotifRepository.findByCollecteFlux(collecte, operateur);

		suiviKafkaNotificationOptional.ifPresentOrElse(suiviKafkaNotification -> {

			metricServiceTicketsRead.inc(stepExecution.getReadCount());
			suiviKafkaNotification.setNbTicketRead(suiviKafkaNotification.getNbTicketRead() + stepExecution.getReadCount());
			suiviKafkaNotification.setLastUpdate(Calendar.getInstance().getTime());

			metricServiceTicketsWrite.inc(stepExecution.getWriteCount());
			suiviKafkaNotification.setNbTicketWritten(suiviKafkaNotification.getNbTicketWritten() + stepExecution.getWriteCount());
			suiviKafkaNotification.setLastUpdate(Calendar.getInstance().getTime());


			long nbFileTreated = suiviKafkaNotification.getNbFileTreated();
			nbFileTreated++;
			metricServiceFileCounter.inc(nbFileTreated);
			try {
				metricServiceFileCounter.pushMetrics();

			} catch (MetricException e) {
				LOGGER.warn("FAILED to  push metrics {}", LocalTime.now());
				
			}
			suiviKafkaNotification.setNbFileTreated(nbFileTreated);
			suiviKafkaNotification.setLastUpdate(Calendar.getInstance().getTime());
			suiviKafkaNotifRepository.save(suiviKafkaNotification);
		}, () -> LOGGER.warn("No SuiviKafkaNotification with Collecte : " + collecte + " and Operateur : " + operateur + " was found"));


		suiviFichierSumo.setEnd(Instant.now().toString());

		try {
			elasticService.save(suiviFichierSumo, "suivi_fichier_sumo");

		} catch (IOException e1) {
			LOGGER.error("create elastic _doc suiviFichierSumo problem: {}", suiviFichierSumo.toString());
		}

		try {
			metricServiceTicketsRead.pushMetrics();
			metricServiceTicketsWrite.pushMetrics();

		} catch (MetricException e) {
			LOGGER.warn("FAILED to  push metrics  {}", LocalTime.now());
			
		}

		LOGGER.info("" + stepExecution.getReadCount());

		return stepExecution.getExitStatus();
	}

	private void feedStepInjectionKafkaAndInjectionElasticSuiviTraitement() {

		Optional<SuiviTraitement> suiviTraitementOptional = suiviTraitementRepository.findByIdTrtmAndNomEtape(idTrtm, "Contrôles données");

		suiviTraitementOptional.ifPresentOrElse(suiviTraitement -> {
			suiviTraitement.setDateFinTrtm(LocalDateTime.now());
			suiviTraitement.setCodeRetr(48);
			suiviTraitement.setLibelleErreur(LIB_ERREUR);
			suiviTraitementRepository.save(suiviTraitement);
		}, () -> LOGGER.warn("No SuiviTraitement with idTrtm : " + idTrtm + " and nomEtape : \"Contrôles données\" was found"));

		SuiviTraitement suiviTraitementInjectionKafka = new SuiviTraitement();
		suiviTraitementInjectionKafka.setIdTrtm(idTrtm);
		suiviTraitementInjectionKafka.setNomTrtm(INJECTION);
		suiviTraitementInjectionKafka.setNomEtape(INJECTION_KAFKA);
		suiviTraitementInjectionKafka.setDateDebtTrtm(LocalDateTime.now());
		suiviTraitementInjectionKafka.setDateFinTrtm(LocalDateTime.now());
		suiviTraitementInjectionKafka.setCodeRetr(49);
		suiviTraitementInjectionKafka.setLibelleErreur(LIB_ERREUR);

		suiviTraitementRepository.save(suiviTraitementInjectionKafka);			

		SuiviTraitement suiviTraitementInjectionElastic = new SuiviTraitement();
		suiviTraitementInjectionElastic.setIdTrtm(idTrtm);
		suiviTraitementInjectionElastic.setNomTrtm(INJECTION);
		suiviTraitementInjectionElastic.setNomEtape(INJECTION_ELASTIC);
		suiviTraitementInjectionElastic.setDateDebtTrtm(LocalDateTime.now());
		suiviTraitementInjectionElastic.setCodeRetr(40);
		suiviTraitementInjectionElastic.setLibelleErreur(LIB_ERREUR);
		suiviTraitementRepository.save(suiviTraitementInjectionElastic);
	}

	private void feedStepFourSuiviAlimentation() {

		Optional<SuiviAlimentation> suiviAlimentationOptional = suiviAlimentationRepository.findById(idTrtm);

		suiviAlimentationOptional.ifPresent(suiviAlimentation -> {

			suiviAlimentation.setDatFinChrg(LocalDateTime.now());

			suiviAlimentation.setTicketPlusRecent(CustomProcessor.TICKET_PLUS_RECENT.equals(LocalDateTime.MIN)?null:CustomProcessor.TICKET_PLUS_RECENT  ) ;
			suiviAlimentation.setTicketPlusVieux(CustomProcessor.TICKET_PLUS_VIEUX.equals(LocalDateTime.MAX)?null:CustomProcessor.TICKET_PLUS_VIEUX);
			suiviAlimentation.setEtat(ETAT_CHRG.getEtat());

			suiviAlimentationRepository.save(suiviAlimentation);
		});
	}
}