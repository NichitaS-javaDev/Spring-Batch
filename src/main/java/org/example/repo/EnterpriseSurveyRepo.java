package org.example.repo;

import org.example.entity.EnterpriseSurvey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnterpriseSurveyRepo extends JpaRepository<EnterpriseSurvey, Integer> {
}
