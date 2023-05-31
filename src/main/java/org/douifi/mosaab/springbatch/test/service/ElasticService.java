package fr.sfr.sumo.xms.srr.alim.service;

import java.io.IOException;

public interface ElasticService <T>{
	/**
	 * 	 * 
	 * @param obj document to save into elaticsearch
	 * @param index index name
	 * @return
	 * @throws IOException
	 */
    boolean save(T doc, String index) throws IOException;
   
    

}
