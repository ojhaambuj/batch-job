package com.cat.gmm.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.cat.gmm.batch.*"})
@EnableBatchProcessing
public class GMMPOBatchApplication implements CommandLineRunner {
	@Qualifier("launcher")
	@Autowired
    JobLauncher jobLauncher;
     
    @Autowired
    Job processJob;

	public static void main(String[] args) {
		SpringApplication.run(GMMPOBatchApplication.class, args);
	}
	
	 @Override
	  public void run(String... args) throws Exception 
	  {
	        JobParameters params = new JobParametersBuilder()
	                    .addString("JobID", String.valueOf(System.currentTimeMillis()))
	                    .toJobParameters();
	        jobLauncher.run(processJob, params);
	    }

}
