package fr.sfr.sumo.xms.srr.alim.listener;

import fr.sfr.sumo.storage.spi.Storage;
import fr.sfr.sumo.storage.spi.exception.EmptyStreamException;
import fr.sfr.sumo.storage.spi.exception.ObjectNotFoundException;
import fr.sfr.sumo.xms.srr.alim.model.SuiviAlimentation;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviAlimentationRepository;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;


public class JobBatchSrrListener implements JobExecutionListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(JobBatchSrrListener.class);
	
	@Autowired
	Storage storage;
	@Value("${file_name}")
	String FILE_NAME;
	@Value("${storage.s3.bucket.rejected.repo}")
	String REJECTED_REPO;
	@Value("${storage.s3.bucket.archives.repo}")
	String ARCHIVES_REPO;
	@Value("${storage.s3.bucket.repo}")
	String FILE_REPO;
	@Value("${directory.tmp.path}")
	String TPM_PATH;
	@Value("${id_trtm}")
    long ID_TRTM;
	 @Autowired
	 SuiviAlimentationRepository suiviAlimentationRepository;

	@Override
	public void beforeJob(@NotNull JobExecution jobExecution) {
	
		
		
	}

	@Override
	public void afterJob(@NotNull JobExecution jobExecution) {
		File tmpRejectedFile = new File(TPM_PATH + FILE_NAME + ".TICKETS_REJETES");
		File tmpcopyFile = new File(TPM_PATH + FILE_NAME);
		File originalFile = new File(FILE_REPO + FILE_NAME);

		if(tmpRejectedFile.exists())
		{
			try {
				storage.put(new FileInputStream(tmpRejectedFile), REJECTED_REPO + FILE_NAME + ".TICKETS_REJETES");
			    tmpRejectedFile.delete();
			} catch (EmptyStreamException | IOException e) {
				throw new RuntimeException(e);
			}
		}else{

				
			LOGGER.info("File of rejected tickets does not exist");
		}
		if(tmpcopyFile.exists())
		{
			try {
				storage.put(new FileInputStream(tmpcopyFile), ARCHIVES_REPO + FILE_NAME);
				storage.delete(FILE_REPO + FILE_NAME);
				tmpcopyFile.delete();
			} catch (EmptyStreamException | IOException | ObjectNotFoundException e) {
				LOGGER.error("storage exception");
			}
		}else{
			LOGGER.info("Copy tickets File does not exist");
		}

		suiviAlimentationRepository.updatSuiviAlimSyncElastic(ID_TRTM);
		

 
	}
}