package com.whataboutmv.settings;


import com.whataboutmv.domain.Account;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

@Data
@NoArgsConstructor
public class Profile {
    //자기 소개
    @Length(max = 35)
    private String bio;
    // 웹 사이트 URL
    @Length(max = 50)
    private String url;
    // 선호하는 영화
    @Length(max = 30)
    private String preferenceKind;
    // 좋아하는 영화배우
    @Length(max = 30)
    private String preferenceActor;
    // 살고있는 지역
    @Length(max = 50)
    private String location;

    public Profile(Account account) {
        this.bio = account.getBio();
        this.url = account.getUrl();
        this.preferenceKind = account.getPreferenceKind();
        this.preferenceActor = account.getPreferenceActor();
        this.location = account.getLocation();
    }
}
