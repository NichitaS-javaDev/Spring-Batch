package org.example.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Entity
@Data
public class EnterpriseSurvey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer Id;
    @Pattern(regexp = "\\b(?:1[0-9]{3}|[2-9][0-9]{3})\\b")
    private Integer year;
    @NotBlank
    private String industryAggregation;
    @NotBlank
    private String industryCode;
    @NotBlank
    private String industryName;
    @NotBlank
    private String units;
    @NotBlank
    private String variableCode;
    @NotBlank
    private String variableName;
    @NotBlank
    private String variableCategory;
    @NotBlank
    private String value;
    @NotBlank
    private String industryCodeDesc;

}
