package com.whataboutmv.modules.account;

import com.whataboutmv.modules.movie.*;
import com.whataboutmv.modules.tag.*;
import com.whataboutmv.modules.zone.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @AllArgsConstructor @NoArgsConstructor
public class Account {

    @Id @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    private String password;

    // 이메일 인증절차 판단
    private boolean emailVerified;

    // 이메일을 검증하기 위한 토큰 값
    private String emailCheckToken;

    //인증된 사용자는 그때 가입이 된 것으로 확인
    private LocalDateTime joinedAt;
    //자기 소개
    private String bio;
    // 웹 사이트 URL
    private String url;
    // 선호하는 영화
    private String preferenceKind;
    // 좋아하는 영화배우
    private String preferenceActor;
    // 살고있는 지역
    private String location;

    // 프로필 이미지는 페치 모드로
    @Lob @Basic(fetch= FetchType.EAGER)
    private String profileImage;

    private boolean comuCreatedByEmail;

    private boolean comuCreatedByWeb = true;

    private boolean comuEnrollmentResultByEmail;

    private boolean comuEnrollmentResultByWeb = true;
    // 바뀐 정보를 받을 것인가
    private boolean comuUpdatedByWeb = true;

    private boolean comuUpdatedByEmail;

    private LocalDateTime emailCheckTokenGeneratedAt;

    @ManyToMany
    private Set<Tag> tags = new HashSet<>();

    @ManyToMany
    private Set<Zone> zones = new HashSet<>();


    public void generateEmailCheckToken() {

        this.emailCheckToken = UUID.randomUUID().toString();
        this.emailCheckTokenGeneratedAt = LocalDateTime.now();
    }


    public void completeSignUp() {
        this.emailVerified = true;
        this.joinedAt = LocalDateTime.now();
    }

    public boolean isValidToken(String token) {
        return this.emailCheckToken.equals(token);
    }

    public boolean canSendConfirmEmail() {
        return this.emailCheckTokenGeneratedAt.isBefore(LocalDateTime.now().minusHours(1));
    }

}

