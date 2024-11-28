package com.survey.www.surveys.repository;

import com.survey.www.surveys.domain.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
}
