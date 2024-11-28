package com.survey.www.surveys.domain;

import com.survey.www.commons.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "설문조사 답변 관리를 위한 도메인 객체")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class SurveyAnswer extends BaseEntity {
    @Schema(title = "고유번호", description = "설문조사 답변 고유번호입니다")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(title = "휴대폰번호", description = "사용자 휴대폰번호")
    @Column(nullable = false)
    private String phone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Builder
    public SurveyAnswer(Long id, String phone, Survey survey) {
        this.id = id;
        this.phone = phone;
        this.survey = survey;
    }
}
