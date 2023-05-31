package fr.sfr.sumo.xms.srr.alim.listener;

import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;
import fr.sfr.sumo.xms.srr.alim.step.CheckRulesFileTasklet;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ReaderValidationListener implements ItemReadListener<XmsSrr> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckRulesFileTasklet.class);
    private final Validator factory = Validation.buildDefaultValidatorFactory().getValidator();
    @Value("${file_name}")
    String FILE_NAME;
    @Value("${directory.tmp.path}")
    String TMP_DIR;

    @Override
    public void beforeRead() {

    }

    @Override
    public void afterRead(@NotNull XmsSrr xmsSrr) {
        Set<ConstraintViolation<XmsSrr>> violations = factory.validate(xmsSrr);
        violations.forEach(v -> LOGGER.error(v.toString()));
    }

    @Override
    public void onReadError(@NotNull Exception e) {

    }
}
