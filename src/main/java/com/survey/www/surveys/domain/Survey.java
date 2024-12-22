package com.survey.www.surveys.domain;

import com.survey.www.accounts.domain.Account;
import com.survey.www.commons.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "설문조사 관리를 위한 도메인 객체")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Survey extends BaseEntity {
    @Schema(title = "고유번호", description = "설문조사 고유번호입니다")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(title = "이름", description = "설문조사 이름입니다")
    @Column(nullable = false)
    private String surveyName;

    @Schema(title = "설명", description = "설문조사 설명입니다")
    @Column(nullable = false, length = 1000)
    private String description;

    @Schema(title = "삭제 여부", description = "삭제 여부입니다")
    @Column(nullable = false)
    private Boolean isDeleted = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    private Survey(Long id, String surveyName, String description, Boolean isDeleted, Account account) {
        this.id = id;
        this.surveyName = surveyName;
        this.description = description;
        this.isDeleted = isDeleted;
        this.account = account;
    }

    public static Survey create(String surveyName, String description, Boolean isDeleted, Account account) {
        return new Survey(null, surveyName, description, isDeleted, account);
    }

    public void update(String surveyName, String description) {
        this.surveyName = surveyName;
        this.description = description;
    }
}
