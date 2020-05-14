package com.whataboutmv.modules.movie;

import com.whataboutmv.infra.AbstractContainerBaseTest;
import com.whataboutmv.infra.MockMvcTest;
import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.account.AccountFactory;
import com.whataboutmv.modules.account.AccountRepository;
import com.whataboutmv.modules.account.WithAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@MockMvcTest
class MovieSettingsControllerTest{

    @Autowired
    MockMvc mockMvc;
    @Autowired MovieFactory movieFactory;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    AccountRepository accountRepository;

    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 소개 수정 폼 조회 - 실패 ( 권한 없는 유저 )")
    void updateDescriptionForm_fail() throws Exception {
        Account winwarm = accountFactory.createAccount("winwarm");
        Movie movie = movieFactory.createMovie("test-comu", winwarm);

        mockMvc.perform(get("/movie/" + movie.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }
    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 소개 수정 폼 조회 - 성공 ( 권한 있는 유저 )")
    void updateDescriptionForm_success() throws Exception {
        Account seungyeol = accountRepository.findByNickname("seungyeol");
        Movie movie = movieFactory.createMovie("test-comu", seungyeol);

        mockMvc.perform(get("/movie/" + movie.getPath() + "/settings/description"))
                .andExpect(status().isOk())
                .andExpect(view().name("movie/settings/description"))
                .andExpect(model().attributeExists("movieDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("movie"));
    }
    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 소개 수정 - 성공")
    void updateDescription_success() throws Exception {
        Account seungyeol = accountRepository.findByNickname("seungyeol");
        Movie movie = movieFactory.createMovie("test-comu", seungyeol);

        String settingsDescriptionURL = "/movie/" + movie.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionURL)
                .param("shortDescription", "short description")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(settingsDescriptionURL))
                .andExpect(flash().attributeExists("message"));
    }
    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 소개 수정 - 실패")
    void updateDescription_fail() throws Exception {
        Account seungyeol = accountRepository.findByNickname("seungyeol");
        Movie movie = movieFactory.createMovie("test-comu", seungyeol);

        String settingsDescriptionURL = "/movie/" + movie.getPath() + "/settings/description";
        mockMvc.perform(post(settingsDescriptionURL)
                .param("shortDescription", "")
                .param("fullDescription", "full description")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("movieDescriptionForm"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("movie"));
    }

}