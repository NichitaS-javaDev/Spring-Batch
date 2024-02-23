package org.example.config;

import org.example.processor.EnterpriseSurveyProcessor;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
@Import({ImportEnterpriseSurveyBatchConfig.class, ExportEnterpriseSurveyBatchConfig.class})
public class MainConfig {
    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(12);

        return asyncTaskExecutor;
    }

    @Bean
    public EnterpriseSurveyProcessor enterpriseSurveyProcessor(){
        return new EnterpriseSurveyProcessor();
    }
}
