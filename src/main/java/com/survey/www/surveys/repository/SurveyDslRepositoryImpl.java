package com.survey.www.surveys.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.survey.www.surveys.dto.command.QSurveyDetailAnswerCommand;
import com.survey.www.surveys.dto.command.SurveyDetailAnswerCommand;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.survey.www.surveys.domain.QSurvey.survey;
import static com.survey.www.surveys.domain.QSurveyAnswerQuestions.surveyAnswerQuestions;
import static com.survey.www.surveys.domain.QSurveyQuestionOptions.surveyQuestionOptions;
import static com.survey.www.surveys.domain.QSurveyQuestions.surveyQuestions;

@RequiredArgsConstructor
public class SurveyDslRepositoryImpl implements SurveyDslRepository {
    private final JPAQueryFactory factory;

    @Override
    public List<SurveyDetailAnswerCommand> searchBySurveyId(Long surveyId) {
        return factory.select(new QSurveyDetailAnswerCommand(
                              surveyQuestions.id
                            , surveyQuestions.surveyQuestionType
                            , surveyQuestions.questionNm
                            , surveyQuestionOptions.id.as("surveyQuestionOptionId")
                            , surveyQuestionOptions.content.as("optionContent")
                            , JPAExpressions.select(Expressions.stringTemplate("JSON_ARRAYAGG({0})", surveyAnswerQuestions.content))
                                            .from(surveyAnswerQuestions)
                                            .where(surveyAnswerQuestions.surveyQuestions.id.eq(surveyQuestions.id)
                                                 , surveyAnswerQuestions.content.isNotEmpty())
                            , JPAExpressions.select(surveyAnswerQuestions.count())
                                            .from(surveyAnswerQuestions)
                                            .where(surveyAnswerQuestions.surveyQuestionOptions.id.eq(surveyQuestionOptions.id))
                            , JPAExpressions.select(surveyQuestionOptions.count())
                                            .from(surveyQuestionOptions)
                                            .where(surveyQuestionOptions.surveyQuestions.id.eq(surveyQuestions.id))))
                      .from(survey)
                      .leftJoin(surveyQuestions).on(surveyQuestions.survey.id.eq(survey.id))
                      .leftJoin(surveyQuestionOptions).on(surveyQuestionOptions.surveyQuestions.id.eq(surveyQuestions.id))
                      .where(survey.id.eq(surveyId))
                      .orderBy(surveyQuestions.id.asc(), surveyQuestionOptions.id.asc())
                      .fetch();
    }
}
