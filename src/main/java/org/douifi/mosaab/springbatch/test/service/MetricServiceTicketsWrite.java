package fr.sfr.sumo.xms.srr.alim.service;

import fr.sfr.sumo.xms.srr.alim.exception.MetricException;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@Setter
public class MetricServiceTicketsWrite {

    @Value("${prometheus.pushgateway.job}")
    String job_name;
    @Value("${prometheus.pushgateway.file_type}")
    String file_type;
    @Value("${prometheus.pushgateway.collect_type}")
    String collect_type;
    @Value("${prometheus.pushgateway.code_app}")
    String code_app;
    @Value("${prometheus.pushgateway.saa_name}")
    String saa_name;


    @Autowired
    private PushGateway pushGateway;
    private CollectorRegistry collectorRegistry;
    private MeterRegistry meterRegistry;
    private Gauge ticketCounter;
 
    
    private Map<String, String> groupingKey = new HashMap<>();


    public MetricServiceTicketsWrite(MeterRegistry meterRegistry, CollectorRegistry collectorRegistry) {
        this.meterRegistry = meterRegistry;
        this.collectorRegistry = collectorRegistry;

        ticketCounter =  Gauge.build()
                .name("spring_batch_writen_items")
                .help("Number of writen items ")
                .register(collectorRegistry);
        
       
    }
    public void inc(double value)  {
        ticketCounter.set(value);
       
    }
    public void pushMetrics() throws MetricException {  
       groupingKey.put("code_app", code_app);
       groupingKey.put("ssa_name", saa_name);
       groupingKey.put("collecte",collect_type );
       groupingKey.put("operateur",file_type );
       try {
           pushGateway.pushAdd(collectorRegistry, "sumo_xms_srr_writen_tickets_all", groupingKey);
       }catch (IOException e){
           throw new MetricException("Can't push metrics to promethuse",e);
       }

    }
}