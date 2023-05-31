package fr.sfr.sumo.xms.srr.alim.service.Impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.sfr.sumo.xms.srr.alim.service.ElasticService;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


public class ElasticServiceImpl <D>implements ElasticService <D>{
		
	private static final Logger logger = LoggerFactory.getLogger(ElasticServiceImpl.class);
	
	private RestClient restClient;
    private RestClientTransport transport ;
    private ElasticsearchClient esClient;
  

    public ElasticServiceImpl(String hostname, String scheme, Integer port) {
        this.restClient = RestClient.builder(new HttpHost(hostname,port)).build();
        this.transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        esClient = new ElasticsearchClient(transport);
      
    }
    
    @Override
    public boolean save(D doc, String index) throws IOException {
    	
      
        ObjectMapper mapper = new ObjectMapper();
        String jsonObj = mapper.writeValueAsString(doc);        
        logger.info("Start inserting doc in {} index  ----------------------->{}",index,jsonObj);     
        IndexResponse response = esClient.index(i -> i
	    .index(index)        		  
	    .document(doc)
        .refresh(Refresh.True)
	);
      
       logger.info("Object indexed with id {}", response.id());
      
        return  true;

    }
    
   
}
