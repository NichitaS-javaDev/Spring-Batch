package org.example.processor;

import jakarta.validation.Valid;
import org.example.entity.EnterpriseSurvey;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;

public class EnterpriseSurveyProcessor implements ItemProcessor<EnterpriseSurvey, EnterpriseSurvey> {
    @Override
    public EnterpriseSurvey process(@Valid @NonNull EnterpriseSurvey survey) {
        return survey;
    }
}
