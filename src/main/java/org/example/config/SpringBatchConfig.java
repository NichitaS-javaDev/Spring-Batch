package org.example.config;

import org.example.entity.EnterpriseSurvey;
import org.example.repo.EnterpriseSurveyRepo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
public class SpringBatchConfig {
    private final EnterpriseSurveyRepo enterpriseSurveyRepo;
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Autowired
    public SpringBatchConfig(EnterpriseSurveyRepo enterpriseSurveyRepo, JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.enterpriseSurveyRepo = enterpriseSurveyRepo;
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public FlatFileItemReader<EnterpriseSurvey> reader() {
        FlatFileItemReader<EnterpriseSurvey> itemReader = new FlatFileItemReader<>();
        itemReader.setResource(new FileSystemResource("src/main/resources/enterprise-survey-csv.csv"));
        itemReader.setName("csvReader");
        itemReader.setLinesToSkip(1);
        itemReader.setLineMapper(lineMapper());

        return itemReader;
    }

    private LineMapper<EnterpriseSurvey> lineMapper() {
        DefaultLineMapper<EnterpriseSurvey> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(
                "Year", "Industry_aggregation", "Industry_code", "Industry_name", "Units", "Variable_code", "Variable_name", "Variable_category", "Value", "Industry_code"
        );

        BeanWrapperFieldSetMapper<EnterpriseSurvey> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(EnterpriseSurvey.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public EnterpriseSurveyProcessor processor() {
        return new EnterpriseSurveyProcessor();
    }

    @Bean
    public RepositoryItemWriter<EnterpriseSurvey> writer() {
        RepositoryItemWriter<EnterpriseSurvey> writer = new RepositoryItemWriter<>();
        writer.setRepository(enterpriseSurveyRepo);
        writer.setMethodName("save");

        return writer;
    }

    @Bean
    public Step step1() {
        return new StepBuilder("csv-step", jobRepository)
                .<EnterpriseSurvey, EnterpriseSurvey>chunk(100, transactionManager)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job runJob() {
        return new JobBuilder("importCustomers", jobRepository)
                .flow(step1())
                .end()
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(12);

        return asyncTaskExecutor;
    }

}
