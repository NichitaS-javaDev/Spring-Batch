package org.example.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jobs")
public class JobController {
    private final Job importSurveysJob;
    private final Job exportSurveysJob;
    private final JobLauncher jobLauncher;

    @Autowired
    public JobController(JobLauncher jobLauncher, Job importSurveysJob, Job exportSurveysJob) {
        this.jobLauncher = jobLauncher;
        this.importSurveysJob = importSurveysJob;
        this.exportSurveysJob = exportSurveysJob;
    }

    @PostMapping("/importEnterpriseSurveys")
    public void importCsvToDBJob() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(importSurveysJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @PostMapping("/exportEnterpriseSurveys")
    public void importUsersToCsv() {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
        try {
            jobLauncher.run(exportSurveysJob, jobParameters);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
