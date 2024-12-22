package com.survey.www.surveys.repository;

import com.survey.www.surveys.dto.command.SurveyDetailAnswerCommand;

import java.util.List;

public interface SurveyDslRepository {
    List<SurveyDetailAnswerCommand> searchBySurveyId(Long surveyId, String questionName, String optionContent, String answerContent);
}
