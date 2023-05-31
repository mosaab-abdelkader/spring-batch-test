package fr.sfr.sumo.xms.srr.alim.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.sfr.sumo.xms.srr.alim.utils.ValidDate;
import fr.sfr.sumo.xms.srr.alim.utils.ValidHeure;


import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.Instant;

public class XmsSrr {
    private String timeStamp;
    
    @Size(max = 1, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^M?$", message = "Not default value M")
    private String TYP_ENRG;
    @Size(max = 17, message = "${javax.validation.constraints.Size.message}")
    private String ID_UNQ;
    @Size(max = 6, message = "${javax.validation.constraints.Size.message}")
    private String REFR_EQPM;
    @Size(max = 2, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    private String TYP_MU;
    @NotNull(message = "Field cannot be null")
    @NotEmpty(message = "Field cannot be empty")
    @Size(max = 16, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    private String IMSI_ABNN;
    @NotNull(message = "Field cannot be null")
    @NotEmpty(message = "Field cannot be empty")
    @Size(max = 15, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    private String ISDN_ABNN;
    @NotNull(message = "Field cannot be null")
    @NotEmpty(message = "Field cannot be empty")
    @Size(max = 8, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    @ValidDate
    private String DAT_DEBT_COMM;
    @NotNull(message = "Field cannot be null")
    @NotEmpty(message = "Field cannot be empty")
    @Size(max = 6, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    @ValidHeure
    private String HEUR_DEBT_COMM;
    @Size(max = 3, message = "${javax.validation.constraints.Size.message}")
    private String SERV_BAS;
    @Size(max = 2, message = "${javax.validation.constraints.Size.message}")
    private String OPTN_APPL;
    @Size(max = 10, message = "${javax.validation.constraints.Size.message}")
    private String CATG_TICK;
    @Size(max = 2, message = "${javax.validation.constraints.Size.message}")
    private String OPTN_LIVR_LECT;
    @Size(max = 2, message = "${javax.validation.constraints.Size.message}")
    private String COD_ZON;
    @Size(max = 8, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    private String TAIL_MESS;
    @Size(max = 1, message = "${javax.validation.constraints.Size.message}")
    private String INDC_PRP;
    @Size(max = 1, message = "${javax.validation.constraints.Size.message}")
    private String INDC_ROAM;
    @Size(max = 3, message = "${javax.validation.constraints.Size.message}")
    private String ID_COMM;
    @Size(max = 1, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    private String bpsSiComType;
    @Size(max = 2, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    private String NOMB_CONT;
    @Size(max = 2, message = "${javax.validation.constraints.Size.message}")
    private String NOMB_CORR;
    private String TO_IGNORE_1;
    @Size(max = 198, message = "${javax.validation.constraints.Size.message}")
    private String TYP_CONT;
    private String TO_IGNORE_2;
    @Size(max = 4, message = "${javax.validation.constraints.Size.message}")
    private String NATR_CORR;
    private String TO_IGNORE_3;
    @Size(max = 999, message = "${javax.validation.constraints.Size.message}")
    private String CORRESPONDANT;
    @Size(max = 1, message = "${javax.validation.constraints.Size.message}")
    private String CONTACT_TYPE;
    @Size(max = 2, message = "${javax.validation.constraints.Size.message}")
    private String CommOpeCode;
    @Size(max = 5, message = "${javax.validation.constraints.Size.message}")
    @Pattern(regexp = "^[\\s\\d\\s]*$", message = "Not numeric")
    private String IMSI_DEST;
    private String TO_IGNORE_4;
    @Size(max = 9, message = "${javax.validation.constraints.Size.message}")
    private String NUMR_FICH;
    private String COLLECTE;
    private String OPERATEUR;
    // Getter & Setter
    @JsonProperty("@timestamp")
    public String getTimeStamp() {
        return Instant.now().toString();
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
    @JsonProperty("typ_enrg")
    public String getTYP_ENRG() {
        return TYP_ENRG;
    }

    public void setTYP_ENRG(String TYP_ENRG) {
        this.TYP_ENRG = TYP_ENRG;
    }
    @JsonProperty("id_unq")
    public String getID_UNQ() {
        return ID_UNQ;
    }

    public void setID_UNQ(String ID_UNQ) {
        this.ID_UNQ = ID_UNQ;
    }
    @JsonProperty("refr_eqpm")
    public String getREFR_EQPM() {
        return REFR_EQPM;
    }

    public void setREFR_EQPM(String REFR_EQPM) {
        this.REFR_EQPM = REFR_EQPM;
    }
    @JsonProperty("typ_mu")
    public String getTYP_MU() {
        return TYP_MU;
    }

    public void setTYP_MU(String TYP_MU) {
        this.TYP_MU = TYP_MU;
    }
    @JsonProperty("imsi_abnn")
    public String getIMSI_ABNN() {
        return IMSI_ABNN;
    }

    public void setIMSI_ABNN(String IMSI_ABNN) {
        this.IMSI_ABNN = IMSI_ABNN;
    }
    @JsonProperty("isdn_abnn")
    public String getISDN_ABNN() {
        return ISDN_ABNN;
    }

    public void setISDN_ABNN(String ISDN_ABNN) {
        this.ISDN_ABNN = ISDN_ABNN;
    }
    @JsonProperty("dat_debt_comm")
    public String getDAT_DEBT_COMM() {
        return DAT_DEBT_COMM;
    }

    public void setDAT_DEBT_COMM(String DAT_DEBT_COMM) {
        this.DAT_DEBT_COMM = DAT_DEBT_COMM;
    }
    @JsonProperty("heur_debt_comm")
    public String getHEUR_DEBT_COMM() {
        return HEUR_DEBT_COMM;
    }

    public void setHEUR_DEBT_COMM(String HEUR_DEBT_COMM) {
        this.HEUR_DEBT_COMM = HEUR_DEBT_COMM;
    }
    @JsonProperty("serv_bas")
    public String getSERV_BAS() {
        return SERV_BAS;
    }

    public void setSERV_BAS(String SERV_BAS) {
        this.SERV_BAS = SERV_BAS;
    }
    @JsonProperty("optn_appl")
    public String getOPTN_APPL() {
        return OPTN_APPL;
    }

    public void setOPTN_APPL(String OPTN_APPL) {
        this.OPTN_APPL = OPTN_APPL;
    }
    @JsonProperty("catg_tick")
    public String getCATG_TICK() {
        return CATG_TICK;
    }

    public void setCATG_TICK(String CATG_TICK) {
        this.CATG_TICK = CATG_TICK;
    }
    @JsonProperty("optn_livr_lect")
    public String getOPTN_LIVR_LECT() {
        return OPTN_LIVR_LECT;
    }

    public void setOPTN_LIVR_LECT(String OPTN_LIVR_LECT) {
        this.OPTN_LIVR_LECT = OPTN_LIVR_LECT;
    }
    @JsonProperty("cod_zon")
    public String getCOD_ZON() {
        return COD_ZON;
    }

    public void setCOD_ZON(String COD_ZON) {
        this.COD_ZON = COD_ZON;
    }
    @JsonProperty("tail_mess")
    public String getTAIL_MESS() {
        return TAIL_MESS;
    }

    public void setTAIL_MESS(String TAIL_MESS) {
        this.TAIL_MESS = TAIL_MESS;
    }
    @JsonProperty("indc_prp")
    public String getINDC_PRP() {
        return INDC_PRP;
    }

    public void setINDC_PRP(String INDC_PRP) {
        this.INDC_PRP = INDC_PRP;
    }
    @JsonProperty("indc_roam")
    public String getINDC_ROAM() {
        return INDC_ROAM;
    }

    public void setINDC_ROAM(String INDC_ROAM) {
        this.INDC_ROAM = INDC_ROAM;
    }
    @JsonProperty("id_comm")
    public String getID_COMM() {
        return ID_COMM;
    }

    public void setID_COMM(String ID_COMM) {
        this.ID_COMM = ID_COMM;
    }
    @JsonProperty("bpssicomtype")
    public String getBpsSiComType() {
        return bpsSiComType;
    }

    public void setBpsSiComType(String bpsSiComType) {
        this.bpsSiComType = bpsSiComType;
    }
    @JsonProperty("nomb_cont")
    public String getNOMB_CONT() {
        return NOMB_CONT;
    }

    public void setNOMB_CONT(String NOMB_CONT) {
        this.NOMB_CONT = NOMB_CONT;
    }
    @JsonProperty("nomb_corr")
    public String getNOMB_CORR() {
        return NOMB_CORR;
    }

    public void setNOMB_CORR(String NOMB_CORR) {
        this.NOMB_CORR = NOMB_CORR;
    }
    @JsonIgnore
    public String getTO_IGNORE_1() {
        return TO_IGNORE_1;
    }

    public void setTO_IGNORE_1(String TO_IGNORE_1) {
        this.TO_IGNORE_1 = TO_IGNORE_1;
    }
    @JsonProperty("typ_cont")
    public String getTYP_CONT() {
        return TYP_CONT;
    }

    public void setTYP_CONT(String TYP_CONT) {
        this.TYP_CONT = TYP_CONT;
    }
    @JsonIgnore
    public String getTO_IGNORE_2() {
        return TO_IGNORE_2;
    }

    public void setTO_IGNORE_2(String TO_IGNORE_2) {
        this.TO_IGNORE_2 = TO_IGNORE_2;
    }
    @JsonProperty("natr_corr")
    public String getNATR_CORR() {
        return NATR_CORR;
    }

    public void setNATR_CORR(String NATR_CORR) {
        this.NATR_CORR = NATR_CORR;
    }
    @JsonIgnore
    public String getTO_IGNORE_3() {
        return TO_IGNORE_3;
    }

    public void setTO_IGNORE_3(String TO_IGNORE_3) {
        this.TO_IGNORE_3 = TO_IGNORE_3;
    }
    @JsonProperty("correspondant")
    public String getCORRESPONDANT() {
        return CORRESPONDANT;
    }

    public void setCORRESPONDANT(String CORRESPONDANT) {
        this.CORRESPONDANT = CORRESPONDANT;
    }
    @JsonProperty("contact_type")
    public String getCONTACT_TYPE() {
        return CONTACT_TYPE;
    }

    public void setCONTACT_TYPE(String CONTACT_TYPE) {
        this.CONTACT_TYPE = CONTACT_TYPE;
    }
    @JsonProperty("commopecode")
    public String getCommOpeCode() {
        return CommOpeCode;
    }

    public void setCommOpeCode(String commOpeCode) {
        CommOpeCode = commOpeCode;
    }
    @JsonProperty("imsi_corr")
    public String getIMSI_DEST() {
        return IMSI_DEST;
    }

    public void setIMSI_DEST(String IMSI_DEST) {
        this.IMSI_DEST = IMSI_DEST;
    }
    @JsonIgnore
    public String getTO_IGNORE_4() {
        return TO_IGNORE_4;
    }

    public void setTO_IGNORE_4(String TO_IGNORE_4) {
        this.TO_IGNORE_4 = TO_IGNORE_4;
    }
    @JsonProperty("numr_fich")
    public String  getNUMR_FICH() {
        return NUMR_FICH;
    }

    public void setNUMR_FICH(String  NUMR_FICH) {
        this.NUMR_FICH = NUMR_FICH;
    }

    @JsonProperty("collecte")
    public String getCOLLECTE() {
        return COLLECTE;
    }

    public void setCOLLECTE(String cOLLECTE) {
        COLLECTE = cOLLECTE;
    }
    @JsonProperty("operateur")
    public String getOPERATEUR() {
        return OPERATEUR;
    }

    public void setOPERATEUR(String oPERATEUR) {
        OPERATEUR = oPERATEUR;
    }

    @Override
    public String toString() {
        return "XmsSrr{" +
            "timeStamp='" + timeStamp + '\'' +
            ", TYP_ENRG='" + TYP_ENRG + '\'' +
            ", ID_UNQ='" + ID_UNQ + '\'' +
            ", REFR_EQPM='" + REFR_EQPM + '\'' +
            ", TYP_MU='" + TYP_MU + '\'' +
            ", IMSI_ABNN='" + IMSI_ABNN + '\'' +
            ", ISDN_ABNN='" + ISDN_ABNN + '\'' +
            ", DAT_DEBT_COMM='" + DAT_DEBT_COMM + '\'' +
            ", HEUR_DEBT_COMM='" + HEUR_DEBT_COMM + '\'' +
            ", SERV_BAS='" + SERV_BAS + '\'' +
            ", OPTN_APPL='" + OPTN_APPL + '\'' +
            ", CATG_TICK='" + CATG_TICK + '\'' +
            ", OPTN_LIVR_LECT='" + OPTN_LIVR_LECT + '\'' +
            ", COD_ZON='" + COD_ZON + '\'' +
            ", TAIL_MESS='" + TAIL_MESS + '\'' +
            ", INDC_PRP='" + INDC_PRP + '\'' +
            ", INDC_ROAM='" + INDC_ROAM + '\'' +
            ", ID_COMM='" + ID_COMM + '\'' +
            ", bpsSiComType='" + bpsSiComType + '\'' +
            ", NOMB_CONT='" + NOMB_CONT + '\'' +
            ", NOMB_CORR='" + NOMB_CORR + '\'' +
            ", TO_IGNORE_1='" + TO_IGNORE_1 + '\'' +
            ", TYP_CONT='" + TYP_CONT + '\'' +
            ", TO_IGNORE_2='" + TO_IGNORE_2 + '\'' +
            ", NATR_CORR='" + NATR_CORR + '\'' +
            ", TO_IGNORE_3='" + TO_IGNORE_3 + '\'' +
            ", CORRESPONDANT='" + CORRESPONDANT + '\'' +
            ", CONTACT_TYPE='" + CONTACT_TYPE + '\'' +
            ", CommOpeCode='" + CommOpeCode + '\'' +
            ", IMSI_DEST='" + IMSI_DEST + '\'' +
            ", TO_IGNORE_4='" + TO_IGNORE_4 + '\'' +
            ", NUMR_FICH='" + NUMR_FICH + '\'' +
            '}';
    }

    public String toCsv() {
        return TYP_ENRG + ";"
            + ID_UNQ + ";"
            + REFR_EQPM + ";"
            + TYP_MU + ";"
            + IMSI_ABNN + ";"
            + ISDN_ABNN + ";"
            + DAT_DEBT_COMM + ";"
            + HEUR_DEBT_COMM + ";"
            + SERV_BAS + ";"
            + OPTN_APPL + ";"
            + CATG_TICK + ";"
            + OPTN_LIVR_LECT + ";"
            + COD_ZON + ";"
            + TAIL_MESS + ";"
            + INDC_PRP + ";"
            + INDC_ROAM + ";"
            + ID_COMM + ";"
            + bpsSiComType + ";"
            + NOMB_CONT + ";"
            + NOMB_CORR + ";"
            + TO_IGNORE_1 + ";"
            + TYP_CONT + ";"
            + TO_IGNORE_2 + ";"
            + NATR_CORR + ";"
            + TO_IGNORE_3 + ";"
            + CORRESPONDANT + ";"
            + CONTACT_TYPE + ";"
            + CommOpeCode + ";"
            + IMSI_DEST + ";"
            + TO_IGNORE_4
                ;
    }
}
