package com.survey.www.surveys.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringTemplate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.survey.www.surveys.dto.command.QSurveyDetailAnswerCommand;
import com.survey.www.surveys.dto.command.SurveyDetailAnswerCommand;
import io.micrometer.common.util.StringUtils;
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
    public List<SurveyDetailAnswerCommand> searchBySurveyId(Long surveyId, String questionName, String optionContent, String answerContent) {
        return factory.select(new QSurveyDetailAnswerCommand(
                              surveyQuestions.id
                            , surveyQuestions.surveyQuestionType
                            , surveyQuestions.questionName
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
                      .where(survey.id.eq(surveyId)
                           , isQuestionNameContains(questionName)
                           , isOptionContentContains(optionContent)
                           , isAnswerContentContains(answerContent)
                      )
                      .orderBy(surveyQuestions.id.asc(), surveyQuestionOptions.id.asc())
                      .fetch();
    }

    private BooleanExpression isQuestionNameContains(String questionName) {
        //if (questionName == null || questionName.isEmpty()) {
        if (StringUtils.isEmpty(questionName)) {
            return null;
        }

        return surveyQuestions.questionName.contains(questionName);
    }

    private BooleanExpression isOptionContentContains(String optionContent) {
        if (optionContent == null || optionContent.isEmpty()) {
            return null;
        }

        return surveyQuestionOptions.content.contains(optionContent);
    }

    private BooleanExpression isAnswerContentContains(String answerContent) {
        if (answerContent == null || answerContent.isEmpty()) {
            return null;
        }

        StringTemplate aggregatedContent = Expressions.stringTemplate("CAST(GROUP_CONCAT({0}) AS STRING)", surveyAnswerQuestions.content);
        return Expressions.stringTemplate("({0})", JPAExpressions.select(aggregatedContent)
                                                                          .from(surveyAnswerQuestions)
                                                                          .where(surveyAnswerQuestions.surveyQuestions.id.eq(surveyQuestions.id)
                                                                               , surveyAnswerQuestions.content.isNotEmpty())).like("%" + answerContent + "%");
    }
}
