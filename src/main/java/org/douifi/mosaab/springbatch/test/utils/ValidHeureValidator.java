package fr.sfr.sumo.xms.srr.alim.utils;

import org.assertj.core.util.Strings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidHeureValidator implements ConstraintValidator<ValidHeure, String> {

    private Boolean isOptional;

    @Override
    public void initialize(ValidHeure validHeure) {
        this.isOptional = validHeure.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {

        boolean validHeure = isValidFormat("HHmmss", value);

        return isOptional ? (validHeure || (Strings.isNullOrEmpty(value))) : validHeure;
    }

    private static boolean isValidFormat(String format, String value) {
        Date heure = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            if (value != null){
                heure = sdf.parse(value);
                if (!value.equals(sdf.format(heure))) {
                    heure = null;
                }
            }

        } catch (ParseException ex) {
        }
        return heure != null;
    }
}