package fr.sfr.sumo.xms.srr.alim.step;

import fr.sfr.sumo.storage.spi.Storage;
import fr.sfr.sumo.storage.spi.exception.EmptyFileNameException;
import fr.sfr.sumo.storage.spi.exception.ObjectNotFoundException;
import fr.sfr.sumo.storage.spi.impl.StorageImpl;
import fr.sfr.sumo.xms.srr.alim.exception.S3ConnectionException;
import fr.sfr.sumo.xms.srr.alim.listener.ReaderValidationListener;
import fr.sfr.sumo.xms.srr.alim.listener.StepBatchSrrListener;
import fr.sfr.sumo.xms.srr.alim.model.XmsSrr;
import fr.sfr.sumo.xms.srr.alim.policy.FileVerificationSkipper;
import fr.sfr.sumo.xms.srr.alim.processor.CustomProcessor;

import fr.sfr.sumo.xms.srr.alim.service.ElasticService;
import fr.sfr.sumo.xms.srr.alim.service.Impl.ElasticServiceImpl;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.prometheus.client.exporter.PushGateway;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.kafka.KafkaItemWriter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import io.prometheus.client.CollectorRegistry;

import java.io.InputStream;


@Configuration
public class StepsConfig {

	@Value("${prometheus.pushgateway.url}")
	String url;
	@Value("${storage.s3.bucket}")
	private String bucketName;
	@Value("${storage.s3.accessKey}")
	private String accessKey;
	@Value("${storage.s3.secretKey}")
	private String secretKey;
	@Value("${storage.s3.endpoint}")
	private String serviceEndpoint;
	@Value("${storage.s3.bucket.repo}")
	private String FILE_REPO;
	@Value("${elastic.host}")
	private String elasticHost;
	@Value("${elastic.scheme}")
	private String elasticScheme;
	@Value("${elastic.port}")
	private String elasticPort;
	@Value("${file_name}")
	String FILE_NAME;
	@Value("${directory.tmp.path}")
	String TMP_DIR;
	@Value("${step.chunk}")
	int chunk;

	@Bean
	public Step checkRulesFileStep(StepBuilderFactory stepBuilders, CheckRulesFileTasklet checkRulesFileTasklet)   {
		return stepBuilders.get("checkRulesFileStep")
			.tasklet(checkRulesFileTasklet)
			.build();
	}

	@Bean
	public CheckRulesFileTasklet checkRulesFileTasklet() {
		Storage storage = storage();
		InputStream inputStream;
		try {
			inputStream = storage.get(FILE_REPO + FILE_NAME);
		} catch (ObjectNotFoundException | EmptyFileNameException e) {
			throw new S3ConnectionException(e.getMessage(), e);
		}

		return (new CheckRulesFileTasklet(inputStream));
	}

	@Bean
	public StepExecutionListener buildStepExecutionListener() {
		return new StepBatchSrrListener();
	}

	@Bean
	public SkipPolicy fileVerificationSkipper() {
		return new FileVerificationSkipper();
	}

	@Bean
	public Step sumoXmsSrrStep(ItemReader<XmsSrr> reader,
							   KafkaItemWriter <String, XmsSrr> kafkaItemWriter, StepBuilderFactory stepBuilderFactory) {
		return stepBuilderFactory.get("sumoXmsSrrStep")

			.<XmsSrr,XmsSrr>chunk(10)
			.reader(reader)
			.listener(new ReaderValidationListener())
			.processor(processor())
			.writer(kafkaItemWriter)
			.faultTolerant()
			.skipLimit(1000)
			.skipPolicy(fileVerificationSkipper())
			.listener(buildStepExecutionListener())
            .build();
	}

	@Bean
	public Storage storage() {
		return new StorageImpl(bucketName, serviceEndpoint, accessKey, secretKey);
	}
	@Bean
	public ElasticService<Object>  elasticService() {

		return new ElasticServiceImpl<Object>(elasticHost,elasticScheme,Integer.parseInt(elasticPort));
	}

	@Bean
	public CustomProcessor processor() {
		return new CustomProcessor();
	}

	@Bean
	public PushGateway pushGateway() {
		return new PushGateway(url);
	}

	@Bean
	public MeterRegistry meterRegistry(PushGateway pushGateway) {
		return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
	}

	@Bean
	public CollectorRegistry collectorRegistry() {
		return new CollectorRegistry();
	}
	


	}
