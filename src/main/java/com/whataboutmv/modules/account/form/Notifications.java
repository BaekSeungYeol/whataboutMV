package com.whataboutmv.modules.account.form;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Notifications {

    private String profileImage;

    private boolean comuCreatedByEmail;

    private boolean comuCreatedByWeb;

    private boolean comuEnrollmentResultByEmail;

    private boolean comuEnrollmentResultByWeb;
    // 바뀐 정보를 받을 것인가
    private boolean comuUpdatedByWeb;

    private boolean comuUpdatedByEmail;

    private LocalDateTime emailCheckTokenGeneratedAt;

    private boolean communityCreatedByWeb;

}
