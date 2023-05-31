package fr.sfr.sumo.xms.srr.alim.reader;

/**
 * SFR SUMO
 * ReaderConfiguration class 
 * Classe de configuration pour la création d'un bean reader 
 *
 * @author mdouifi
 * @version 1.0
 * @since 2023-03-13
 *
 */

import fr.sfr.sumo.storage.spi.Storage;
import fr.sfr.sumo.storage.spi.exception.EmptyFileNameException;
import fr.sfr.sumo.storage.spi.exception.ObjectNotFoundException;
import fr.sfr.sumo.xms.srr.alim.exception.S3ConnectionException;
import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;
import java.time.LocalTime;

@Configuration
public class ReaderConfiguration {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderConfiguration.class);
	@Value("${storage.s3.bucket.repo}")
	private String bucketRepo;
	@Value("${file_name}")
	String FILE_NAME;
	@Autowired
	Storage storage ;

	/**
	 *
	 * @return ItemReader
	 */
	@Bean
	public ItemReader<XmsSrr> itemReader() throws S3ConnectionException, ObjectNotFoundException, EmptyFileNameException {
		LOGGER.info("Kafka item reader started  {}", LocalTime.now());

		InputStream inputStream = storage.get(bucketRepo + FILE_NAME);

		LineMapper<XmsSrr> xmsSrrLineMapper = createXmsSrrLineMapper();

		ReaderValidation readerValidation = new ReaderValidation();
		readerValidation.setName("xmsSrrReader");
		readerValidation.setResource(new InputStreamResource(inputStream));
		readerValidation.setLinesToSkip(1);
		readerValidation.setLineMapper(xmsSrrLineMapper);

		return readerValidation;
	}

	/**
	 *
	 * @return LineMapper
	 */
	private LineMapper<XmsSrr> createXmsSrrLineMapper() {
		DefaultLineMapper<XmsSrr> xmsSrrLineMapper = new DefaultLineMapper<>();

		LineTokenizer xmsSrrLineTokenizer = createXmsSrrLineTokenizer();
		xmsSrrLineMapper.setLineTokenizer(xmsSrrLineTokenizer);

		FieldSetMapper<XmsSrr> xmsSrrInformationMapper = createXmsSrrInformationMapper() ;
		xmsSrrLineMapper.setFieldSetMapper(xmsSrrInformationMapper);

		return xmsSrrLineMapper;
	}

	/**
	 *
	 * @return LineTokenizer
	 */
	private LineTokenizer createXmsSrrLineTokenizer() {
		DelimitedLineTokenizer xmsSrrLineTokenizer = new DelimitedLineTokenizer();
		xmsSrrLineTokenizer.setDelimiter(";");
		xmsSrrLineTokenizer.setNames(new String[]{"TYP_ENRG","ID_UNQ","REFR_EQPM","TYP_MU","IMSI_ABNN"
			,"ISDN_ABNN","DAT_DEBT_COMM","HEUR_DEBT_COMM","SERV_BAS","OPTN_APPL","CAT_TICK","OPTN_LIVR_LECT",
			"COD_ZON","TAIL_MESS","INDC_PRP","INDC_ROAM","ID_COMM","bpsSiComType","NOMB_CONT","NOMB_CORR","TO_IGNORE_1",
			"TYP_CONT","TO_IGNORE_2","NATR_CORR","TO_IGNORE_3","CORRESPONDANT","CONTACT_TYPE","CommOpeCode","IMSI_DEST","TO_IGNORE_4"
		});
		return xmsSrrLineTokenizer;
	}
	/**
	 *
	 * @return FieldSetMapper
	 */
	private FieldSetMapper<XmsSrr> createXmsSrrInformationMapper() {
		BeanWrapperFieldSetMapper<XmsSrr> xmsSrrInformationMapper = new BeanWrapperFieldSetMapper<>();
		xmsSrrInformationMapper.setTargetType(XmsSrr.class);
		return xmsSrrInformationMapper;
	}
}
