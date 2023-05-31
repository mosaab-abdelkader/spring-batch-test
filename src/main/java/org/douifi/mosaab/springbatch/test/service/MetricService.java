package fr.sfr.sumo.xms.srr.alim.service;

import fr.sfr.sumo.xms.srr.alim.exception.MetricException;
import io.micrometer.core.instrument.MeterRegistry;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Gauge;
import io.prometheus.client.exporter.PushGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetricService {

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
    PushGateway pushGateway;
    private final CollectorRegistry collectorRegistry;
    private MeterRegistry meterRegistry;
    private final Gauge ticketCounter;
    private final Map<String, String> groupingKey = new HashMap<>();


    public MetricService(MeterRegistry meterRegistry, CollectorRegistry collectorRegistry) {
        this.meterRegistry = meterRegistry;
        this.collectorRegistry = collectorRegistry;

        ticketCounter =  Gauge.build()
                .name("spring_batch_items_written")
                .help("Number of items written")
                .register(collectorRegistry);
    }
    public void inc() throws InterruptedException {
        ticketCounter.inc();
    }
    public void pushMetrics() throws MetricException {  
       groupingKey.put("code_app", code_app);
       groupingKey.put("ssa_name", saa_name);
       groupingKey.put("collect_type", collect_type);
       groupingKey.put("file_type", file_type);
       try {
           pushGateway.pushAdd(collectorRegistry, job_name, groupingKey);
       } catch (IOException e){
           throw new MetricException("Can't push metrics to prometheus", e);
       }

    }
}