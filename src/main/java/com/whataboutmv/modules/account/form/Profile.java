package com.whataboutmv.modules.account.form;


import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
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

    private String profileImage;


}
