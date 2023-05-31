package fr.sfr.sumo.xms.srr.alim.step;

import fr.sfr.sumo.storage.spi.Storage;
import fr.sfr.sumo.storage.spi.exception.EmptyStreamException;
import fr.sfr.sumo.xms.srr.alim.model.FileError;
import fr.sfr.sumo.xms.srr.alim.model.SuiviAlimentation;
import fr.sfr.sumo.xms.srr.alim.model.SuiviKafkaNotification;
import fr.sfr.sumo.xms.srr.alim.model.SuiviTraitement;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviAlimentationRepository;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviKafkaNotifRepository;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviTraitementRepository;
import fr.sfr.sumo.xms.srr.alim.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Optional;

import static fr.sfr.sumo.xms.srr.alim.model.EtatEnum.*;
import static fr.sfr.sumo.xms.srr.alim.model.FileError.*;
@Setter
@Getter
public class CheckRulesFileTasklet implements Tasklet {
    private static final Logger LOGGER = LoggerFactory.getLogger(CheckRulesFileTasklet.class);
    
    private static final String INJECTION = "Injection";
    private static final String RECEPTION = "Réception";
    private static final String CONTROLE_FICHIER = "Contrôle du fichier";
    private static final String CONTROLE_DONNEES = "Contrôles données";
    private static final String NOTIFICATION = "Notification";

    @Value("${sumo.collecte}")
    String COLLECTE;
    @Value("${sumo.operateur}")
    String OPERATEUR;
    @Value("${file_name}")
    String FILE_NAME;
    @Value("${id_trtm}")
    long ID_TRTM;
    @Value("${numr_fich}")
    int numrFich;
    @Value("${directory.tmp.path}")
    String TMP_PATH;
    @Value("${storage.s3.bucket.rejected.repo}")
    String REJECTED_REPO;
    @Autowired
    SuiviAlimentationRepository suiviAlimentationRepository;
    @Autowired
    SuiviTraitementRepository suiviTraitementRepository;
    @Autowired
    SuiviKafkaNotifRepository suiviKafkaNotifRepository;
    @Autowired
    Storage storage;
    private File tmpcopyFile;
    private InputStream  inputStreamFluxFromS3;
    private String chksFich;
    private String firstLine;
    private String lastLine;
    private int nombLignAttn = 0;
    private Integer lineCounter;
    private int codeRetr = 50;
    private String libelleErreur = "Etape OK";
    private File fileForChksFich;

    public CheckRulesFileTasklet(InputStream inputStreamFluxFromS3) {
        this.inputStreamFluxFromS3 = inputStreamFluxFromS3;
    }

    @Override
    public RepeatStatus execute(@NotNull StepContribution contribution, @NotNull ChunkContext chunkContext) throws Exception {

        LOGGER.info("Check Rules File Tasklet execute started  {}", LocalTime.now());
        prepareFileToExecuteRules();

        LOGGER.info("Creating tmp file which contain all lines without header and footer {}", LocalTime.now());
        chksFich = FileUtils.computeChecksum(fileForChksFich.getPath());
        fileForChksFich.delete();

        feedStepTwoSuiviAlimentation(firstLine);
        feedStepControleDuFichierSuiviTraitement();

        boolean correctLine = false;
        try {
            correctLine = Integer.parseInt(lastLine.substring(1)) == lineCounter;
            nombLignAttn = Integer.parseInt(lastLine.substring(1));
            correctLine = nombLignAttn == lineCounter;
        } catch (final NumberFormatException e) {
            LOGGER.error("Footer has invalid format or is missing {}", LocalTime.now());
        }
        boolean chksFichAlreadyNotExisting = suiviAlimentationRepository.findByChksFichWhereEtatIsNotAnnl(chksFich).size() == 0;
        boolean nomFichAlreadyNotExisting = suiviAlimentationRepository.findByNomFichWhereEtatIsNotAnnl(FILE_NAME).size() == 0;
        if (correctLine) {

        	  if ( nomFichAlreadyNotExisting ) {

                  if (chksFichAlreadyNotExisting) {
                      LOGGER.info("Check Rules File Tasklet SUCCEEDED {}", LocalTime.now());

                      feedStepControlesDonneesSuiviTraitement(true);
                      feedStepThreeSuiviAlimentation(chksFich, true);

                  } else {

                  codeRetr = CONTENT_ALREADY_EXISTS.getCode();
                  libelleErreur = CONTENT_ALREADY_EXISTS.getMessage();
                  handleCheckRulesErrorCases("File with same content Already EXISTS in the database", CONTENT_ALREADY_EXISTS.getSuffix());

                  contribution.setExitStatus(ExitStatus.COMPLETED);
                  chunkContext.getStepContext().getStepExecution().setTerminateOnly();
                      
                  }

              } else {

                 codeRetr =  DOUBLE_FILE_RECEIVED.getCode();
                 libelleErreur = DOUBLE_FILE_RECEIVED.getMessage();
                 handleCheckRulesErrorCases("File Already EXIST in the database", DOUBLE_FILE_RECEIVED.getSuffix());
                 LOGGER.error("FileNameAlreadyExistsException {}", "Check Rules File FAILED");
                     
                      contribution.setExitStatus(ExitStatus.COMPLETED);
                      chunkContext.getStepContext().getStepExecution().setTerminateOnly();

                  
              }
        } else {
            codeRetr = FOOTER_IS_INVALID.getCode();
            libelleErreur = FOOTER_IS_INVALID.getMessage();
            handleCheckRulesErrorCases("File lines doesn't match with footer", FOOTER_IS_INVALID.getSuffix());

            LOGGER.error("FooterMismatchingLineCounterException {}", "Check Rules File FAILED");
           
            contribution.setExitStatus(ExitStatus.COMPLETED);
            chunkContext.getStepContext().getStepExecution().setTerminateOnly();
           
            
        }
        
        return RepeatStatus.FINISHED;
    }

    public  void prepareFileToExecuteRules() throws IOException {

        tmpcopyFile = new File(TMP_PATH + FILE_NAME);
        FileUtils.InputStreamToFile(inputStreamFluxFromS3, tmpcopyFile);

        lineCounter = (int) FileUtils.lineCounts(tmpcopyFile);
        firstLine = FileUtils.getFirstLine(tmpcopyFile);
        lastLine = FileUtils.getLastLine(tmpcopyFile);

        LOGGER.info("\n** Number of lines in File **  {}", lineCounter);

        fileForChksFich = FileUtils.fileWithoutFirstAndLastLine(tmpcopyFile);
    }


    private void handleCheckRulesErrorCases(String errorLog, String errorFileExtension) throws IOException, EmptyStreamException {

        LOGGER.warn("Check Rules File Tasklet FAILED | " + errorLog + " {}", LocalTime.now());

        Optional<SuiviKafkaNotification> suiviKafkaNotificationOptional = suiviKafkaNotifRepository.findByCollecteFlux(COLLECTE, OPERATEUR);

        suiviKafkaNotificationOptional.ifPresentOrElse(suiviKafkaNotification -> {
            suiviKafkaNotification.setNbFileReject(suiviKafkaNotification.getNbFileReject() + 1);
            suiviKafkaNotification.setLastUpdate(Calendar.getInstance().getTime());
            suiviKafkaNotifRepository.save(suiviKafkaNotification);
        }, () -> LOGGER.warn("No SuiviKafkaNotification with Collecte : " + COLLECTE + " and Operateur : " + OPERATEUR + " was found"));

        feedStepControlesDonneesSuiviTraitement(false);
        feedStepThreeSuiviAlimentation(chksFich, false);

        storage.put(new FileInputStream(tmpcopyFile),REJECTED_REPO + FILE_NAME + errorFileExtension);
        tmpcopyFile.delete();
    }

    private void feedStepControleDuFichierSuiviTraitement() {
        Optional<SuiviTraitement> suiviTraitementOptional = suiviTraitementRepository.findByIdTrtmAndNomEtape(ID_TRTM, NOTIFICATION);

        suiviTraitementOptional.ifPresentOrElse(suiviTraitement -> {
            suiviTraitement.setDateFinTrtm(LocalDateTime.now());
            suiviTraitement.setCodeRetr(codeRetr);
            suiviTraitement.setLibelleErreur(libelleErreur);

            suiviTraitementRepository.save(suiviTraitement);
        }, () -> LOGGER.warn("No SuiviTraitement with idTrtm : " + ID_TRTM + " and nomEtape : \"Notification\" was found"));

        SuiviTraitement suiviTraitement = new SuiviTraitement();
        suiviTraitement.setIdTrtm(ID_TRTM);
        suiviTraitement.setNomTrtm(RECEPTION);
        suiviTraitement.setNomEtape(CONTROLE_FICHIER);
        suiviTraitement.setDateDebtTrtm(LocalDateTime.now());
        suiviTraitement.setCodeRetr(47);
        suiviTraitement.setLibelleErreur(libelleErreur);
        suiviTraitementRepository.save(suiviTraitement);
    }

    private void feedStepControlesDonneesSuiviTraitement(boolean isFileValid) {
        Optional<SuiviTraitement> suiviTraitementOptional = suiviTraitementRepository.findByIdTrtmAndNomEtape(ID_TRTM, "Contrôle du fichier");

        suiviTraitementOptional.ifPresentOrElse(suiviTraitement -> {
            suiviTraitement.setDateFinTrtm(LocalDateTime.now());
            suiviTraitement.setCodeRetr(codeRetr);
            suiviTraitement.setLibelleErreur(libelleErreur);
            suiviTraitementRepository.save(suiviTraitement);
        }, () -> LOGGER.warn("No SuiviTraitement with idTrtm : " + ID_TRTM + " and nomEtape : \"Contrôle du fichier\" was found"));

        if (isFileValid) {
            SuiviTraitement suiviTraitement = new SuiviTraitement();
            suiviTraitement.setIdTrtm(ID_TRTM);
            suiviTraitement.setNomTrtm(INJECTION);
            suiviTraitement.setDateDebtTrtm(LocalDateTime.now());
            suiviTraitement.setNomEtape(CONTROLE_DONNEES);

            suiviTraitementRepository.save(suiviTraitement);
        }
    }

    private void feedStepTwoSuiviAlimentation(String firstLine) {
        Optional<SuiviAlimentation> suiviAlimentationOptional = suiviAlimentationRepository.findById(ID_TRTM);

        suiviAlimentationOptional.ifPresent(suiviAlimentation -> {
            try {
                String unparsedDate = firstLine.substring(7, 19);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
                LocalDateTime datExtrFich = LocalDateTime.parse(unparsedDate, formatter);
                suiviAlimentation.setDateRecp(LocalDateTime.now());
                suiviAlimentation.setNumrFich(numrFich);
                suiviAlimentation.setDatExtrFich(datExtrFich);
            } catch (DateTimeParseException e) {
                LOGGER.warn(e.getMessage());
            }

            suiviAlimentation.setDateRecp(LocalDateTime.now());
            suiviAlimentation.setEtat(ETAT_RECU.getEtat());

            suiviAlimentationRepository.save(suiviAlimentation);
        });
    }

    private void feedStepThreeSuiviAlimentation(String chksFich, boolean isFileValid) {

        Optional<SuiviAlimentation> suiviAlimentationOptional = suiviAlimentationRepository.findById(ID_TRTM);


        suiviAlimentationOptional.ifPresentOrElse(suiviAlimentation -> {

            suiviAlimentation.setNomFich(FILE_NAME);
            suiviAlimentation.setChksFich(chksFich);
            suiviAlimentation.setNombLignAttn(nombLignAttn);
            suiviAlimentation.setNombLignReelFich(lineCounter - 2);

            if (isFileValid) {
                suiviAlimentation.setDatDebtChrg(LocalDateTime.now());
                suiviAlimentation.setEtat(ETAT_CTRL.getEtat());
            } else {
                suiviAlimentation.setDateFin(LocalDateTime.now());
                suiviAlimentation.setEtat(ETAT_REJT.getEtat());
            }

            suiviAlimentationRepository.save(suiviAlimentation);
        }, () -> LOGGER.warn("No SuiviAlimentation with Id : " + ID_TRTM + " was found"));
    }
}




