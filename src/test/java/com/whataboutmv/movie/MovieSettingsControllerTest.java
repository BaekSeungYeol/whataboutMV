package com.whataboutmv.movie;

import com.whataboutmv.WithAccount;
import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Movie;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
class MovieSettingsControllerTest extends MovieControllerTest {

    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 소개 수정 폼 조회 - 실패 ( 권한 없는 유저 )")
    void updateDescriptionForm_fail() throws Exception {
        Account winwarm = createAccount("winwarm");
        Movie movie = createMovie("test-comu", winwarm);

        mockMvc.perform(get("/movie/" + movie.getPath() + "/settings/description"))
                .andExpect(status().isForbidden());
    }
    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 소개 수정 폼 조회 - 성공 ( 권한 있는 유저 )")
    void updateDescriptionForm_success() throws Exception {
        Account seungyeol = accountRepository.findByNickname("seungyeol");
        Movie movie = createMovie("test-comu", seungyeol);

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
        Movie movie = createMovie("test-comu", seungyeol);

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
        Movie movie = createMovie("test-comu", seungyeol);

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