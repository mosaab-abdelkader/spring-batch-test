package fr.sfr.sumo.xms.srr.alim.writer;

import fr.sfr.sumo.xms.srr.alim.exception.MetricException;
import fr.sfr.sumo.xms.srr.alim.model.SuiviAlimentation;
import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviAlimentationRepository;
import fr.sfr.sumo.xms.srr.alim.service.MetricService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.kafka.KafkaItemWriter;
import org.springframework.batch.item.kafka.builder.KafkaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;




@Configuration
public class KafkaWriterConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger(KafkaWriterConfig.class);
	@Value("${id_trtm}")
	long ID_TRTM;
	@Autowired
	KafkaTemplate<String, XmsSrr> kafkaTemplate;

	@Autowired
	SuiviAlimentationRepository suiviAlimentationRepository;
	@Autowired
	MetricService metricService;
	
	private long nbTicketChrg=0;

	public KafkaWriterConfig() {
	}

	/**
	 *
	 * @return KafkaItemWriter
	 */
	@Bean
	public KafkaItemWriter<String, XmsSrr> kafkaItemWriter() {
		LOGGER.info("Kafka item writer started  {}", LocalTime.now());

		return new KafkaItemWriterBuilder<String, XmsSrr>()
			.kafkaTemplate(kafkaTemplate)
			.itemKeyMapper(source -> {
				try {
					metricService.inc();
					metricService.pushMetrics();					
					nbTicketChrg++;
					
					
					//if (nbTicketChrg==100) {
						
						suiviAlimentationRepository.incSuiviAlientation(ID_TRTM);
						//Optional<SuiviAlimentation> suiviAlimentationOptional = suiviAlimentationRepository.findByIdTrtm(ID_TRTM);
						//suiviAlimentationOptional.ifPresentOrElse(suiviAlimentation -> {
							

						
								
							//int NombLignChrg = suiviAlimentation.getNombLignChrg();
							//LOGGER.info("Readen from suiviAlimentation --> {}",NombLignChrg);
							//NombLignChrg++;
							
							//suiviAlimentation.setNombLignChrg(NombLignChrg);
							//LOGGER.info("written in suiviAlimentation --> {}",NombLignChrg);
							//suiviAlimentationRepository.saveAndFlush(suiviAlimentation);
						
							
						//}, () -> LOGGER.warn("No SuiviAlimentation with idTrtm : " + ID_TRTM + " was found"));
					
					
			

				} catch (InterruptedException | MetricException e) {
					LOGGER.warn("FAILED to increment counter or push metrics  {}", LocalTime.now());

					throw new RuntimeException();
				}
				LOGGER.info("Write Line in kafka & Set the key of Kafka item writer map with random ID ");
				return UUID.randomUUID().toString();
			})
			.build();
	}

}
