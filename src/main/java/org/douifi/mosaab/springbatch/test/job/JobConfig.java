package fr.sfr.sumo.xms.srr.alim.job;

import fr.sfr.sumo.xms.srr.alim.listener.JobBatchSrrListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobConfig {
	

@Bean
public JobBatchSrrListener jobBatchSrrListener() {
    return new JobBatchSrrListener();
}
    /**
     *
     * @param sumoXmsSrrStep
     * @param jobBuilderFactory
     * @return Job
     */
    @Bean
    public Job sumoXmsSrrJob(Step sumoXmsSrrStep, Step checkRulesFileStep, JobBuilderFactory jobBuilderFactory) {
        return jobBuilderFactory.get("sumoXmsSrrJob")
                .incrementer(new RunIdIncrementer())
                .start(checkRulesFileStep)
                .next(sumoXmsSrrStep)              
                .listener(jobBatchSrrListener())                
                .build();
    }
    
   
}
