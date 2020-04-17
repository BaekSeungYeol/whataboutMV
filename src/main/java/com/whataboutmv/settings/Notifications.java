package com.whataboutmv.settings;

import com.whataboutmv.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    public Notifications(Account account) {
        this.communityCreatedByWeb = account.isComuCreatedByWeb();
        this.comuCreatedByEmail = account.isComuCreatedByEmail();
        this.comuEnrollmentResultByEmail = account.isComuEnrollmentResultByEmail();
        this.comuEnrollmentResultByWeb = account.isComuUpdatedByEmail();
        this.comuUpdatedByEmail = account.isComuUpdatedByEmail();
        this.comuUpdatedByWeb = account.isComuUpdatedByWeb();
    }
}
