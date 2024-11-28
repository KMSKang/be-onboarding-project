package com.survey.www.surveys.controller;

import com.survey.www.commons.config.validation.ValidationSequence;
import com.survey.www.commons.response.CommonResponse;
import com.survey.www.surveys.dto.request.SurveyAnswerCreateRequest;
import com.survey.www.surveys.dto.request.SurveyCreateRequest;
import com.survey.www.surveys.dto.request.SurveyUpdateRequest;
import com.survey.www.surveys.dto.response.SurveyAnswerCreateResponse;
import com.survey.www.surveys.dto.response.SurveyCreateResponse;
import com.survey.www.surveys.dto.response.SurveyDetailResponse;
import com.survey.www.surveys.dto.response.SurveyUpdateResponse;
import com.survey.www.surveys.service.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("/api/v1/surveys")
@RestController
public class SurveyApiController {
    private final SurveyService surveyService;

    @PostMapping
    public ResponseEntity<CommonResponse<SurveyCreateResponse>> createQuestion(@RequestBody @Validated(ValidationSequence.class) SurveyCreateRequest surveyCreateRequest) {
        return ResponseEntity.ok(new CommonResponse<>(surveyService.createQuestion(surveyCreateRequest)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<SurveyDetailResponse>> detailSurveyQuestion(@PathVariable("id") Long surveyId) {
        return ResponseEntity.ok(new CommonResponse<>(surveyService.detailSurveyQuestion(surveyId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommonResponse<SurveyUpdateResponse>> update(@PathVariable("id") Long surveyId, @RequestBody @Validated(ValidationSequence.class) SurveyUpdateRequest surveyUpdateRequest) {
        return ResponseEntity.ok(new CommonResponse<>(surveyService.update(surveyId, surveyUpdateRequest)));
    }

    @PostMapping("/{id}/answer")
    public ResponseEntity<CommonResponse<SurveyAnswerCreateResponse>> createAnswer(@PathVariable("id") Long surveyId, @RequestBody @Validated(ValidationSequence.class) SurveyAnswerCreateRequest surveyAnswerCreateRequest) {
        return ResponseEntity.ok(new CommonResponse<>(surveyService.createAnswer(surveyId, surveyAnswerCreateRequest)));
    }
}
