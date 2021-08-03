/**
 * 
 */
package com.cat.gmm.batch.config;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.SimpleJobExplorer;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.MapJobRepositoryFactoryBean;
import org.springframework.batch.support.transaction.ResourcelessTransactionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cat.gmm.batch.listner.FileListener;
import com.cat.gmm.batch.model.TocsDTO;
import com.cat.gmm.batch.processor.CancelPOProcessor;
import com.cat.gmm.batch.processor.GMMPOProcessor;
import com.cat.gmm.batch.reader.CancelPOReader;
import com.cat.gmm.batch.reader.GMMPOReader;
import com.cat.gmm.batch.writer.CancelPOWriter;
import com.cat.gmm.batch.writer.GMMPOWriter;

/**
 * @author ojhaak
 *
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	@Autowired
	public FileListener fileListener;
	
	@Autowired
	public GMMPOReader gmmPOReader;
	@Autowired
	public GMMPOProcessor gmmPOProcessor;
	
	@Autowired
	public CancelPOReader cancelPOReader;
	
	@Autowired
	public CancelPOProcessor cancelPOProcessor;
	
	@Autowired
	public CancelPOWriter cancelPOWriter;
	
	@Autowired
	public GMMPOWriter gmmPOWriter;

	

	@Bean
	public Job processJob() {

		return jobBuilderFactory.get("processJob").incrementer(new RunIdIncrementer()).listener(fileListener)
				 .start(gmmPOReaderStep())
				 //.next(readPOCancelOrder())
				 .build();
	}

	@Bean
	public Step gmmPOReaderStep() {
		return stepBuilderFactory.get("gmmPOReaderStep").<List<TocsDTO>, List<TocsDTO>>chunk(1)
				.reader(gmmPOReader).processor(gmmPOProcessor).writer(gmmPOWriter).build();
		}
	
	@Bean
	public Step readPOCancelOrder() {
		return stepBuilderFactory.get("cancelPOReaderStep").<List<TocsDTO>, List<TocsDTO>>chunk(1)
				.reader(cancelPOReader).processor(cancelPOProcessor).writer(cancelPOWriter).build();
		}


	@Bean("rstx")
	public ResourcelessTransactionManager transactionManager() {
		return new ResourcelessTransactionManager();
	}

	@Bean
	public MapJobRepositoryFactoryBean mapJobRepositoryFactory(
			@Qualifier("rstx") ResourcelessTransactionManager txManager) throws Exception {
		MapJobRepositoryFactoryBean factory = new MapJobRepositoryFactoryBean(txManager);
		factory.afterPropertiesSet();
		return factory;
	}

	@Bean
	public JobRepository jobRepository(MapJobRepositoryFactoryBean factory) throws Exception {
		return factory.getObject();
	}

	@Bean
	public JobExplorer jobExplorer(MapJobRepositoryFactoryBean factory) {
		return new SimpleJobExplorer(factory.getJobInstanceDao(), factory.getJobExecutionDao(),
				factory.getStepExecutionDao(), factory.getExecutionContextDao());
	}

	@Bean(name = "launcher")
	public SimpleJobLauncher jobLauncher(JobRepository jobRepository) {
		SimpleJobLauncher launcher = new SimpleJobLauncher();
		launcher.setJobRepository(jobRepository);
		return launcher;
	}

	

}
