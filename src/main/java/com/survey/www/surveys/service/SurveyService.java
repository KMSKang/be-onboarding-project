package com.survey.www.surveys.service;

import com.survey.www.accounts.domain.Account;
import com.survey.www.accounts.service.AccountService;
import com.survey.www.surveys.code.SurveyQuestionType;
import com.survey.www.surveys.domain.*;
import com.survey.www.surveys.dto.command.SurveyDetailCommand;
import com.survey.www.surveys.dto.request.SurveyAnswerCreateRequest;
import com.survey.www.surveys.dto.request.SurveyCreateRequest;
import com.survey.www.surveys.dto.request.SurveyUpdateRequest;
import com.survey.www.surveys.dto.response.SurveyAnswerCreateResponse;
import com.survey.www.surveys.dto.response.SurveyCreateResponse;
import com.survey.www.surveys.dto.response.SurveyDetailResponse;
import com.survey.www.surveys.dto.response.SurveyUpdateResponse;
import com.survey.www.surveys.exception.SurveyException;
import com.survey.www.surveys.exception.SurveyExceptionResult;
import com.survey.www.surveys.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class SurveyService {
    private final AccountService accountService;
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionsRepository surveyQuestionsRepository;
    private final SurveyQuestionsOptionsRepository surveyQuestionsOptionsRepository;
    private final SurveyAnswerRepository surveyAnswerRepository;
    private final SurveyAnswerQuestionsRepository surveyAnswerQuestionsRepository;

    @Transactional
    public SurveyCreateResponse createQuestion(SurveyCreateRequest surveyCreateRequest) {
        validateSurveyQuestions(surveyCreateRequest.getSurveyQuestions().size());
        Survey savedSurvey = savedSurvey(surveyCreateRequest);
        savedSurveyQuestions(surveyCreateRequest.getSurveyQuestions(), savedSurvey);
        return new SurveyCreateResponse(savedSurvey.getId());
    }

    @Transactional(readOnly = true)
    public SurveyDetailResponse detailSurveyQuestion(Long surveyId) {
        Survey findSurvey = findSurveyById(surveyId);
        List<SurveyDetailCommand> surveyDetailCommands = surveyQuestionsRepository.searchBySurveyId(surveyId);
        List<SurveyDetailResponse.SurveyQuestionCommand> surveyQuestions = createSurveyQuestionCommands(surveyDetailCommands);
        return createSurveyDetailResponse(findSurvey, surveyQuestions);
    }

    @Transactional
    public SurveyUpdateResponse update(Long surveyId, SurveyUpdateRequest surveyUpdateRequest) {
        Survey findSurvey = findSurveyById(surveyId);
//        validateAccount(findSurvey.getAccount().getId());
        updateSurvey(findSurvey, surveyUpdateRequest);
        updateSurveyQuestions(surveyUpdateRequest.getSurveyQuestions());
        return new SurveyUpdateResponse(surveyId);
    }

    @Transactional
    public SurveyAnswerCreateResponse createAnswer(Long surveyId, SurveyAnswerCreateRequest surveyAnswerCreateRequest) {
        Survey findSurvey = findSurveyById(surveyId);
        SurveyAnswer savedSurveyAnswer = savedSurveyAnswer(findSurvey, surveyAnswerCreateRequest);
        savedSurveyAnswerQuestions(surveyAnswerCreateRequest.getSurveyAnswerQuestions(), savedSurveyAnswer);
        return new SurveyAnswerCreateResponse(savedSurveyAnswer.getId());
    }

    private SurveyAnswer savedSurveyAnswer(Survey findSurvey, SurveyAnswerCreateRequest surveyAnswerCreateRequest) {
        return surveyAnswerRepository.save(createSurveyAnswer(findSurvey, surveyAnswerCreateRequest));
    }

    private static SurveyAnswer createSurveyAnswer(Survey findSurvey, SurveyAnswerCreateRequest surveyAnswerCreateRequest) {
        return surveyAnswerCreateRequest.toEntity(findSurvey);
    }

    private void savedSurveyAnswerQuestions(List<SurveyAnswerCreateRequest.SurveyAnswerQuestionCommand> surveyAnswerQuestions, SurveyAnswer savedSurveyAnswer) {
        for (SurveyAnswerCreateRequest.SurveyAnswerQuestionCommand surveyAnswerQuestionCommand : surveyAnswerQuestions) {
            SurveyQuestions findSurveyQuestion = findSurveyQuestionById(surveyAnswerQuestionCommand.getSurveyQuestionId());

            List<SurveyAnswerQuestions> surveyAnswerQuestionsList = new ArrayList<>();
            if (SurveyQuestionType.SHORT_ANSWER == surveyAnswerQuestionCommand.getSurveyQuestionType() || SurveyQuestionType.LONG_ANSWER == surveyAnswerQuestionCommand.getSurveyQuestionType()) {
                surveyAnswerQuestionsList.add(createSurveyAnswerQuestion(surveyAnswerQuestionCommand, savedSurveyAnswer, findSurveyQuestion, null));
            }
            else {
                for (Long surveyQuestionOptionId : surveyAnswerQuestionCommand.getSurveyQuestionOptionIds()) {
                    surveyAnswerQuestionsList.add(createSurveyAnswerQuestion(surveyAnswerQuestionCommand, savedSurveyAnswer, findSurveyQuestion, findSurveyQuestionOptionById(surveyQuestionOptionId)));
                }
            }
            surveyAnswerQuestionsRepository.saveAll(surveyAnswerQuestionsList);
        }
    }

    private void validateAccount(Long accountId) {
        Account loginAccount = accountService.getLoginAccount();
        if (!loginAccount.getId().equals(accountId)) {
            throw new SurveyException(SurveyExceptionResult.UNAUTHORIZED_SURVEY_MODIFICATION);
        }
    }

    private void updateSurvey(Survey findSurvey, SurveyUpdateRequest surveyUpdateRequest) {
        findSurvey.update(surveyUpdateRequest.getSurveyNm(), surveyUpdateRequest.getDescription());
    }

    private void updateSurveyQuestions(List<SurveyUpdateRequest.SurveyQuestionCommand> surveyQuestionCommands) {
        for (SurveyUpdateRequest.SurveyQuestionCommand surveyQuestionCommand : surveyQuestionCommands) {
            SurveyQuestions findSurveyQuestion = findSurveyQuestionById(surveyQuestionCommand.getId());
            findSurveyQuestion.update(
                    surveyQuestionCommand.getQuestionNm(),
                    surveyQuestionCommand.getDescription(),
                    surveyQuestionCommand.getSurveyQuestionType(),
                    surveyQuestionCommand.getIsRequired(),
                    surveyQuestionCommand.getIsDeleted()
            );

            for (SurveyUpdateRequest.SurveyQuestionOptionCommand surveyQuestionOptionCommand : surveyQuestionCommand.getSurveyQuestionOptions()) {
                SurveyQuestionOptions findSurveyQuestionOption = findSurveyQuestionOptionById(surveyQuestionOptionCommand.getId());
                findSurveyQuestionOption.update(surveyQuestionOptionCommand.getContent(), surveyQuestionOptionCommand.getIsDeleted());
            }
        }
    }

    private SurveyAnswerQuestions createSurveyAnswerQuestion(SurveyAnswerCreateRequest.SurveyAnswerQuestionCommand surveyAnswerQuestionCommand, SurveyAnswer savedSurveyAnswer, SurveyQuestions findSurveyQuestion, SurveyQuestionOptions findSurveyQuestionOption) {
        return surveyAnswerQuestionCommand.toEntity(savedSurveyAnswer, findSurveyQuestion, findSurveyQuestionOption);
    }

    private void validateSurveyQuestions(int surveyQuestionsCount) {
        if (surveyQuestionsCount < 1 || surveyQuestionsCount > 10) {
            throw new SurveyException(SurveyExceptionResult.INVALID_COUNT_SURVEY_QUESTIONS);
        }
    }

    private Survey savedSurvey(SurveyCreateRequest formCreateRequest) {
        return surveyRepository.save(createSurvey(formCreateRequest));
    }

    private Survey createSurvey(SurveyCreateRequest formCreateRequest) {
        return formCreateRequest.toEntity(accountService.getLoginAccount());
    }

    private void savedSurveyQuestions(List<SurveyCreateRequest.SurveyQuestionCommand> surveyQuestionRequests, Survey survey) {
        for (SurveyCreateRequest.SurveyQuestionCommand surveyQuestionRequest : surveyQuestionRequests) {
            SurveyQuestions savedSurveyQuestions = savedSurveyQuestions(survey, surveyQuestionRequest);
            savedSurveyQuestionsOptions(surveyQuestionRequest, savedSurveyQuestions);
        }
    }

    private void savedSurveyQuestionsOptions(SurveyCreateRequest.SurveyQuestionCommand surveyQuestionRequest, SurveyQuestions savedSurveyQuestions) {
        surveyQuestionsOptionsRepository.saveAll(createSurveyQuestionOptions(surveyQuestionRequest.getSurveyQuestionOptions(), savedSurveyQuestions));
    }

    private SurveyQuestions savedSurveyQuestions(Survey survey, SurveyCreateRequest.SurveyQuestionCommand surveyQuestionRequest) {
        return surveyQuestionsRepository.save(createSurveyQuestions(survey, surveyQuestionRequest));
    }

    private SurveyQuestions createSurveyQuestions(Survey survey, SurveyCreateRequest.SurveyQuestionCommand surveyQuestionRequest) {
        return surveyQuestionRequest.toEntity(survey);
    }

    private List<SurveyQuestionOptions> createSurveyQuestionOptions(List<SurveyCreateRequest.SurveyQuestionOptionCommand> surveyQuestionOptions, SurveyQuestions savedSurveyQuestions) {
        return surveyQuestionOptions.stream()
                                    .map(optionRequest -> optionRequest.toEntity(savedSurveyQuestions))
                                    .collect(Collectors.toList());
    }

    private static List<SurveyDetailResponse.SurveyQuestionCommand> createSurveyQuestionCommands(List<SurveyDetailCommand> surveyDetailCommands) {
        List<SurveyDetailResponse.SurveyQuestionCommand> resultList = new ArrayList<>();

        Map<Long, List<SurveyDetailCommand>> groupedByQuestionId = surveyDetailCommands.stream().collect(Collectors.groupingBy(SurveyDetailCommand::surveyQuestionId));
        for (Map.Entry<Long, List<SurveyDetailCommand>> entry : groupedByQuestionId.entrySet()) {
            List<SurveyDetailCommand> commands = entry.getValue();
            if (commands.isEmpty()) {
                continue;
            }

            Long surveyQuestionId = entry.getKey();
            List<SurveyDetailResponse.SurveyQuestionOptionCommand> surveyQuestionOptions = new ArrayList<>();
            for (SurveyDetailCommand command : commands) {
                if (command.surveyQuestionOptionParentId() != null && command.surveyQuestionOptionParentId().equals(surveyQuestionId)) {
                    surveyQuestionOptions.add(
                            SurveyDetailResponse.SurveyQuestionOptionCommand.builder()
                                                                            .id(command.surveyQuestionOptionId())
                                                                            .content(command.surveyQuestionOptionContent())
                                                                            .isDeleted(command.isDeletedSurveyQuestionOption())
                                                                            .build()
                    );
                }
            }

            SurveyDetailCommand firstCommand = commands.get(0);
            resultList.add(
                    SurveyDetailResponse.SurveyQuestionCommand.builder()
                                                              .id(surveyQuestionId)
                                                              .surveyQuestionType(firstCommand.surveyQuestionType())
                                                              .questionNm(firstCommand.surveyQuestionNm())
                                                              .description(firstCommand.surveyQuestionDescription())
                                                              .isRequired(firstCommand.isRequired())
                                                              .isDeleted(firstCommand.isDeletedSurveyQuestion())
                                                              .surveyQuestionOptions(surveyQuestionOptions)
                                                              .build()
            );
        }

        return resultList;
    }

    private static SurveyDetailResponse createSurveyDetailResponse(Survey survey, List<SurveyDetailResponse.SurveyQuestionCommand> surveyQuestions) {
        return SurveyDetailResponse.builder()
                                   .surveyNm(survey.getSurveyNm())
                                   .description(survey.getDescription())
                                   .isDeleted(survey.getIsDeleted())
                                   .surveyQuestions(surveyQuestions)
                                   .build();
    }

    private Survey findSurveyById(Long id) {
        Survey survey = surveyRepository.findById(id).orElseThrow(() -> new SurveyException(SurveyExceptionResult.NOT_FOUND_SURVEY));
        if (survey.getIsDeleted()) {
            throw new SurveyException(SurveyExceptionResult.NOT_EXIST_SURVEY);
        }
        return survey;
    }

    private SurveyQuestions findSurveyQuestionById(Long id) {
        SurveyQuestions surveyQuestions = surveyQuestionsRepository.findById(id).orElseThrow(() -> new SurveyException(SurveyExceptionResult.NOT_FOUND_SURVEY_QUESTION));
        if (surveyQuestions.getIsDeleted()) {
            throw new SurveyException(SurveyExceptionResult.NOT_EXIST_SURVEY_QUESTION);
        }
        return surveyQuestions;
    }

    private SurveyQuestionOptions findSurveyQuestionOptionById(Long id) {
        SurveyQuestionOptions surveyQuestionOptions = surveyQuestionsOptionsRepository.findById(id).orElseThrow(() -> new SurveyException(SurveyExceptionResult.NOT_FOUND_SURVEY_QUESTION));
        if (surveyQuestionOptions.getIsDeleted()) {
            throw new SurveyException(SurveyExceptionResult.NOT_EXIST_SURVEY_QUESTION_OPTION);
        }
        return surveyQuestionOptions;
    }
}
