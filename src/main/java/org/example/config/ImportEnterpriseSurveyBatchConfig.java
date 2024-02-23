package org.example.config;

import org.example.entity.EnterpriseSurvey;
import org.example.processor.EnterpriseSurveyProcessor;
import org.example.repo.EnterpriseSurveyRepo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Configuration
public class ImportEnterpriseSurveyBatchConfig {
    private final EnterpriseSurveyRepo enterpriseSurveyRepo;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final TaskExecutor taskExecutor;
    private final EnterpriseSurveyProcessor enterpriseSurveyProcessor;

    public ImportEnterpriseSurveyBatchConfig(
            EnterpriseSurveyRepo enterpriseSurveyRepo, JobRepository jobRepository, PlatformTransactionManager transactionManager,
            TaskExecutor taskExecutor, EnterpriseSurveyProcessor enterpriseSurveyProcessor
    ) {
        this.enterpriseSurveyRepo = enterpriseSurveyRepo;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.taskExecutor = taskExecutor;
        this.enterpriseSurveyProcessor = enterpriseSurveyProcessor;
    }

    @Bean
    public FlatFileItemReader<EnterpriseSurvey> enterpriseSurveyReader() {
        FlatFileItemReader<EnterpriseSurvey> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/unprocessed/enterprise-survey-csv.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(enterpriseSurveyLineMapper());

        return itemReader;
    }

    private LineMapper<EnterpriseSurvey> enterpriseSurveyLineMapper() {
        DefaultLineMapper<EnterpriseSurvey> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(
                "Year", "Industry_aggregation", "Industry_code", "Industry_name", "Units", "Variable_code", "Variable_name", "Variable_category", "Value", "Industry_code_desc"
        );

        BeanWrapperFieldSetMapper<EnterpriseSurvey> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(EnterpriseSurvey.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public RepositoryItemWriter<EnterpriseSurvey> enterpriseSurveyWriter() {
        RepositoryItemWriter<EnterpriseSurvey> writer = new RepositoryItemWriter<>();
        writer.setRepository(enterpriseSurveyRepo);
        writer.setMethodName("save");

        return writer;
    }

    @Bean
    public Step processFileStep() {
        return new StepBuilder("csv-step", jobRepository)
                .<EnterpriseSurvey, EnterpriseSurvey>chunk(100, transactionManager)
                .reader(enterpriseSurveyReader())
                .processor(enterpriseSurveyProcessor)
                .writer(enterpriseSurveyWriter())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Step moveFileStep() {
        return new StepBuilder("move-step", jobRepository)
                .tasklet(moveFileTasklet(), transactionManager)
                .build();
    }

    private Tasklet moveFileTasklet() {
        return (contribution, chunkContext) -> {
            Path sourcePath = Paths.get("src/main/resources/unprocessed/enterprise-survey-csv.csv");
            Path destinationPath = Paths.get("src/main/resources/processed/enterprise-survey-csv.csv");

            Files.move(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            return RepeatStatus.FINISHED;
        };
    }

    @Bean
    public Job importSurveysJob() {
        return new JobBuilder("importSurveysJob", jobRepository)
                .flow(processFileStep())
                .next(moveFileStep())
                .end()
                .build();
    }

}
