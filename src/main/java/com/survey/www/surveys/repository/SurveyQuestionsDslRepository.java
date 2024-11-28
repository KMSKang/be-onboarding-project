package com.survey.www.surveys.repository;

import com.survey.www.surveys.dto.command.SurveyDetailCommand;

import java.util.List;

public interface SurveyQuestionsDslRepository {
    List<SurveyDetailCommand> searchBySurveyId(Long id);
}
