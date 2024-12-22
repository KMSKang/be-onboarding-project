package com.survey.www.accounts.domain;

import com.survey.www.accounts.code.RoleType;
import com.survey.www.commons.domain.BaseEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원 관리를 위한 도메인 객체")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Account extends BaseEntity {
    @Schema(title = "고유번호", description = "회원 고유번호")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Schema(title = "사용자 권한", description = "사용자 권한은 RESPONDER 로 자동 입력됩니다", example = "RESPONDER")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType roleType = RoleType.RESPONDER;

    @Schema(title = "아이디", description = "사용자 아이디 (휴대폰번호)")
    @Column(nullable = false)
    private String phone;

    @Schema(title = "비밀번호", description = "사용자 비밀번호")
    @Column(nullable = false)
    private String userPassword;

    private Account(Long id, RoleType roleType, String phone, String userPassword) {
        this.id = id;
        this.roleType = roleType;
        this.phone = phone;
        this.userPassword = userPassword;
    }

    public static Account create(RoleType roleType, String phone, String userPassword) {
        return new Account(null, roleType, phone, userPassword);
    }

    public static Account login(Long id, RoleType roleType) {
        return new Account(id, roleType, null, null);
    }
}
