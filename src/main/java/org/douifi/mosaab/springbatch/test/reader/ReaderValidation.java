package fr.sfr.sumo.xms.srr.alim.reader;

import fr.sfr.sumo.xms.srr.alim.exception.FieldMissingException;
import fr.sfr.sumo.xms.srr.alim.exception.TicketValidationException;
import fr.sfr.sumo.xms.srr.alim.model.SuiviAlimentation;
import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;
import fr.sfr.sumo.xms.srr.alim.model.XmsSrrElastic;
import fr.sfr.sumo.xms.srr.alim.repository.SuiviAlimentationRepository;
import fr.sfr.sumo.xms.srr.alim.service.ElasticService;
import fr.sfr.sumo.xms.srr.alim.step.CheckRulesFileTasklet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

public class ReaderValidation extends FlatFileItemReader<XmsSrr> {
	@Value("${id_trtm}")
	long idTrtm;
	@Value("${numr_fich}")
	long numrFich;
	@Autowired
	ElasticService elasticService;
	@Autowired
	SuiviAlimentationRepository suiviAlimentationRepository;

	private boolean doNextCheckViolation=false;

	private static final Logger LOGGER = LoggerFactory.getLogger(ReaderValidation.class);
	private final Validator factory = Validation.buildDefaultValidatorFactory().getValidator();

	@Override
	public XmsSrr doRead() throws Exception {
		XmsSrr xmsSrr = super.doRead();

		if (Objects.isNull(xmsSrr))
			return null;

		Set<ConstraintViolation<XmsSrr>> violations = this.factory.validate(xmsSrr);  



		if (violations.size()!=0) {
			diplayViolations(violations);
			checkNotNullOrNotEmptyViolation(violations,xmsSrr);

	if(doNextCheckViolation) {
		updateWhenExceptTicket( xmsSrr) ;
		}
		}


		return xmsSrr;
	}


	private void updateWhenExceptTicket( XmsSrr xmsSrr) throws TicketValidationException, IOException {


		String[] dateSplit = xmsSrr.getDAT_DEBT_COMM().split("(?<=\\G..)");
		xmsSrr.setDAT_DEBT_COMM(dateSplit[0]+dateSplit[1]+"-"+dateSplit[2]+"-"+dateSplit[3]);
		String[] heureSplit = xmsSrr.getHEUR_DEBT_COMM().split("(?<=\\G..)");
		xmsSrr.setHEUR_DEBT_COMM(heureSplit[0]+":"+heureSplit[1]+":"+heureSplit[2]);


		XmsSrrElastic xmsSrrElatic = new XmsSrrElastic();
		BeanUtils.copyProperties(xmsSrr, xmsSrrElatic);
		xmsSrrElatic.setCOLLECTE("XMS");
		xmsSrrElatic.setOPERATEUR("SRR");
		xmsSrrElatic.setNUMR_FICH(String.valueOf(numrFich));

		Optional<SuiviAlimentation> suiviAlimentation = suiviAlimentationRepository.findById(idTrtm);
		suiviAlimentation.ifPresent(obj ->
		{
			int nbRejtExcep = obj.getNombLignRejtExcp();
			nbRejtExcep++;
			obj.setNombLignRejtExcp(nbRejtExcep);
			suiviAlimentationRepository.save(obj);
		}
				);

		elasticService.save(xmsSrrElatic, "except_sumo_xms_srr");

		String errorMsg = String.format("The input has validation failed. Data is '%s'", xmsSrr);
		doNextCheckViolation=true;
		throw new TicketValidationException(errorMsg);



	}
	private  void diplayViolations(Set<ConstraintViolation<XmsSrr>> violations){
		for( ConstraintViolation<XmsSrr> violation: violations) {
			LOGGER.error(violation.getPropertyPath()+ ":" +violation.getMessage());
		}
	}


	private void checkNotNullOrNotEmptyViolation(Set<ConstraintViolation<XmsSrr>> violations, XmsSrr xmsSrr) throws FieldMissingException, TicketValidationException, IOException {

		boolean containsNullDate = violations.stream().anyMatch(violation -> 
			(violation.getMessage().equals("Field cannot be null") || violation.getMessage().equals("Field cannot be empty")) 
		         && 
		        ( violation.getPropertyPath().toString().equals("HEUR_DEBT_COMM")
		         ||
		         violation.getPropertyPath().toString().equals("DAT_DEBT_COMM"))
		);
		
		
		if (containsNullDate) {
			
			throwError(xmsSrr) ;	
		}
		
		
		for( ConstraintViolation<XmsSrr> violation: violations) {

			if (violation.getMessage().equals("Field cannot be null") || violation.getMessage().equals("Field cannot be empty")) {

				if(violation.getPropertyPath().toString().equals("IMSI_ABNN")) {


					if( xmsSrr.getISDN_ABNN().isEmpty() || xmsSrr.getISDN_ABNN().isBlank()){

						LOGGER.error(xmsSrr + " ---------------------- " + violation);
						throwError(xmsSrr) ;
					}else doNextCheckViolation = false;

				}else if(violation.getPropertyPath().toString().equals("ISDN_ABNN")) {

					if( xmsSrr.getIMSI_ABNN().isEmpty() || xmsSrr.getIMSI_ABNN().isBlank()){

						LOGGER.error(xmsSrr + " ---------------------- " + violation);
						throwError(xmsSrr) ;
					}else doNextCheckViolation = false;

				}else {

					LOGGER.error(xmsSrr + " ---------------------- " + violation);
					throwError(xmsSrr) ;
				}

				
		} else {
			
			updateWhenExceptTicket( xmsSrr) ;
			
		}
		}


	}




	void throwError(XmsSrr xmsSrr) throws FieldMissingException {

		doNextCheckViolation=false;
		throw new FieldMissingException(xmsSrr, String.format("The input has at least one non-nullable field that is missing. Data is '%s'", xmsSrr));


	}
}
