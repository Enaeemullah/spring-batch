package com.springbatch.springbatch.controller;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/batch")
public class Controller {

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier("job1")
    private Job job1;

    @Autowired
    @Qualifier("job2")
    private Job job2;

    @PostMapping("/import-csv")
    public void importCsvData(){

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis())
                .toJobParameters();

        try {
            JobExecution jobExecution = jobLauncher.run(job1, jobParameters);

            if(jobExecution.getStatus() == BatchStatus.COMPLETED){
                JobParameters jobParameter = new JobParametersBuilder()
                        .addLong("startJOB2", System.currentTimeMillis())
                        .toJobParameters();
                jobLauncher.run(job2, jobParameter);
            }
            else {
                System.err.println("JOB Failed:");
            }

        } catch (JobInstanceAlreadyCompleteException | JobExecutionAlreadyRunningException | JobParametersInvalidException | JobRestartException e) {
            throw new RuntimeException(e);
        }
    }
}
