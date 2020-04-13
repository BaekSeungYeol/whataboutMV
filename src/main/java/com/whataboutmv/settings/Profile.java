package com.whataboutmv.settings;


import com.whataboutmv.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Profile {
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

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.preferenceKind = account.getPreferenceKind();
        this.preferenceActor = account.getPreferenceActor();
        this.location = account.getLocation();
    }
}
