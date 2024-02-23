package org.example.mapper;

import org.example.entity.EnterpriseSurvey;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnterpriseSurveyRowMapper implements RowMapper<EnterpriseSurvey> {

    @Override
    public EnterpriseSurvey mapRow(ResultSet rs, int rowNum) throws SQLException {
        EnterpriseSurvey enterpriseSurvey = new EnterpriseSurvey();
        enterpriseSurvey.setId(rs.getInt("id"));
        enterpriseSurvey.setYear(rs.getInt("year"));
        enterpriseSurvey.setIndustryAggregation(rs.getString("industry_aggregation"));
        enterpriseSurvey.setIndustryCode(rs.getString("industry_code"));
        enterpriseSurvey.setIndustryName(rs.getString("industry_name"));
        enterpriseSurvey.setUnits(rs.getString("units"));
        enterpriseSurvey.setVariableCode(rs.getString("variable_code"));
        enterpriseSurvey.setVariableName(rs.getString("variable_name"));
        enterpriseSurvey.setVariableCategory(rs.getString("variable_category"));
        enterpriseSurvey.setValue(rs.getString("value"));
        enterpriseSurvey.setIndustryCodeDesc(rs.getString("industry_code_desc"));

        return enterpriseSurvey;
    }

}
