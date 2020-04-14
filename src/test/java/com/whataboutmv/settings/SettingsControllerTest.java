package com.whataboutmv.settings;

import com.whataboutmv.WithAccount;
import com.whataboutmv.account.AccountRepository;
import com.whataboutmv.account.AccountService;
import com.whataboutmv.account.SignUpForm;
import com.whataboutmv.domain.Account;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
class SettingsControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;


    @AfterEach
    void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    @DisplayName("프로필 수정 폼")
    @WithAccount("seungyeol")
    void updateProfileForm() throws Exception {

        String bio = "짧은 소개 수정하는 경우.";

        mockMvc.perform(get(SettingsController.SETTINGS_PROFILE_URL))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().attributeExists("account"));
    }


    @Test
    @DisplayName("프로필 수정 하기 - 입력값 정상")
    @WithAccount("seungyeol")
    void updateProfile() throws Exception {

        String bio = "짧은 소개 수정하는 경우.";

        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/seungyeol"))
                .andExpect(flash().attributeExists("message"));

        Account account = accountRepository.findByNickname("seungyeol");
        assertEquals(bio, account.getBio());
    }

    @Test
    @DisplayName("프로필 수정 하기 - 입력값 에러")
    @WithAccount("seungyeol")
    void updateProfile_error() throws Exception {

        String bio = "길이가 넘는 bio 길이가 넘는 bio 길이가 넘는 bio 길이가 넘는 bio 길이가 넘는 bio 길이가 넘는 bio";

        mockMvc.perform(post(SettingsController.SETTINGS_PROFILE_URL)
                .param("bio", bio)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name(SettingsController.SETTINGS_PROFILE_VIEW_NAME))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("profile"))
                .andExpect(model().hasErrors());
        Account account = accountRepository.findByNickname("seungyeol");
        assertNull(account.getBio());
    }
}