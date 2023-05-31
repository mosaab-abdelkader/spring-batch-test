package fr.sfr.sumo.xms.srr.alim.model;

import java.time.Instant;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@Component
@JsonIgnoreProperties(ignoreUnknown=true)
public class SuiviFichierSumo {
	
	private String timeStamp;
    private String collecte;
    private long count;
    private String end;
    private String file;
    private String Operateur;
    private String start;
    
    @JsonProperty("@timestamp")
	public String getTimeStamp() {
		 return Instant.now().toString();
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	@JsonProperty("collecte")
	public String getCollecte() {
		return collecte;
	}
	public void setCollecte(String collecte) {
		this.collecte = collecte;
	}
	@JsonProperty("count")
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	@JsonProperty("end")
	public String getEnd() {
		return end;
	}
	public void setEnd(String end) {
		this.end = end;
	}
	@JsonProperty("file")
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	@JsonProperty("operateur")
	public String getOperateur() {
		return Operateur;
	}
	public void setOperateur(String operateur) {
		Operateur = operateur;
	}
	@JsonProperty("start")
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}

	@Override
	public String toString() {
		return "SuiviFichierSumo [timeStamp=" + timeStamp + ", collecte=" + collecte + ", count=" + count + ", end="
				+ end + ", file=" + file + ", Operateur=" + Operateur + ", start=" + start + "]";
	}
    
    
    

}
