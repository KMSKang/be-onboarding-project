package com.survey.www.surveys.dto.request;

import com.survey.www.surveys.code.SurveyQuestionType;
import com.survey.www.surveys.domain.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Builder
@Getter
public class SurveyAnswerCreateRequest {
    @Schema(title = "휴대폰번호", description = "사용자 휴대폰번호")
    private String phone;

    @Schema(title = "항목", description = "답변을 받을 항목입니다")
    private List<SurveyAnswerQuestionCommand> surveyAnswerQuestions;

    public SurveyAnswer toEntity(Survey survey) {
        return SurveyAnswer.builder()
                           .phone(phone)
                           .survey(survey)
                           .build();
    }

    @Builder
    @Getter
    public static class SurveyAnswerQuestionCommand {
        @Schema(title = "고유번호", description = "답변 고유번호입니다")
        private Long surveyAnswerId;

        @Schema(title = "필수 여부", description = "항목 필수 여부입니다")
        private Boolean isRequired;

        @Schema(title = "입력 형태", description = "항목 입력 형태입니다")
        private SurveyQuestionType surveyQuestionType;

        @Schema(title = "내용", description = "답변 내용입니다")
        private String content;

        @Schema(title = "고유번호", description = "항목 고유번호입니다")
        private Long surveyQuestionId;

        @Schema(title = "고유번호", description = "리스트 고유번호입니다")
        private List<Long> surveyQuestionOptionIds;

        public SurveyAnswerQuestions toEntity(SurveyAnswer surveyAnswer, SurveyQuestions surveyQuestions, SurveyQuestionOptions surveyQuestionOptions) {
            return SurveyAnswerQuestions.builder()
                                        .content(content)
                                        .surveyAnswer(surveyAnswer)
                                        .surveyQuestions(surveyQuestions)
                                        .surveyQuestionOptions(surveyQuestionOptions)
                                        .build();
        }
    }
}
