package org.example.config;

import org.example.entity.EnterpriseSurvey;
import org.example.mapper.EnterpriseSurveyRowMapper;
import org.example.processor.EnterpriseSurveyProcessor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class ExportEnterpriseSurveyBatchConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final TaskExecutor taskExecutor;
    private final EnterpriseSurveyProcessor enterpriseSurveyProcessor;

    @Autowired
    public ExportEnterpriseSurveyBatchConfig(
            JobRepository jobRepository, PlatformTransactionManager transactionManager, DataSource dataSource,
            TaskExecutor taskExecutor, EnterpriseSurveyProcessor enterpriseSurveyProcessor
    ) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
        this.dataSource = dataSource;
        this.taskExecutor = taskExecutor;
        this.enterpriseSurveyProcessor = enterpriseSurveyProcessor;
    }

    @Bean
    public JdbcCursorItemReader<EnterpriseSurvey> jdbcCursorItemReader(){
        JdbcCursorItemReader<EnterpriseSurvey> reader = new JdbcCursorItemReader<>();
        reader.setDataSource(dataSource);
        reader.setSql("select * from spring_batch.enterprise_survey_1 union all select * from spring_batch.enterprise_survey_2");
        reader.setVerifyCursorPosition(false);
        reader.setRowMapper(new EnterpriseSurveyRowMapper());

        return reader;
    }

    @Bean
    public FlatFileItemWriter<EnterpriseSurvey> flatFileItemWriter(){
        FlatFileItemWriter<EnterpriseSurvey> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource("src/main/resources/enterpriseSurvey.csv"));
        writer.setLineAggregator(new DelimitedLineAggregator<>() {{
            setDelimiter(",");
            setFieldExtractor(new BeanWrapperFieldExtractor<>() {{
                setNames(new String[] {
                        "id", "year", "industryAggregation", "industryCode", "industryName", "units", "variableCode",
                        "variableName", "variableCategory", "value", "industryCodeDesc"
                });
            }});
        }});

        return writer;
    }

    @Bean
    public Step exportSurveysStep() {
        return new StepBuilder("export-surveys", jobRepository)
                .<EnterpriseSurvey, EnterpriseSurvey>chunk(100, transactionManager)
                .reader(jdbcCursorItemReader())
                .processor(enterpriseSurveyProcessor)
                .writer(flatFileItemWriter())
                .taskExecutor(taskExecutor)
                .build();
    }

    @Bean
    public Job exportSurveysJob() {
        return new JobBuilder("exportUserJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .flow(exportSurveysStep())
                .end()
                .build();
    }

}
