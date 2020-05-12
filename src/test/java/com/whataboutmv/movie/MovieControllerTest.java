package com.whataboutmv.movie;

import com.whataboutmv.WithAccount;
import com.whataboutmv.account.AccountRepository;
import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Movie;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class MovieControllerTest {

    @Autowired protected MockMvc mockMvc;
    @Autowired protected MovieService movieService;
    @Autowired protected MovieRepository movieRepository;
    @Autowired protected AccountRepository accountRepository;


    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 개설 폼 조회")
    void createMovieForm() throws Exception {
        mockMvc.perform(get("/new-movie"))
                .andExpect(status().isOk())
                .andExpect(view().name("movie/form"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("movieForm"));

    }

    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 개설 완료")
    void createMovie_success() throws Exception {
        mockMvc.perform(post("/new-movie")
                .param("path", "test-path")
                .param("title", "movie title")
                .param("shortDescription", "short description of a movie")
                .param("fullDescription", "full description of a movie")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/test-path"));

        Movie movie = movieRepository.findByPath("test-path");
        assertNotNull(movie);
        Account account = accountRepository.findByNickname("seungyeol");
        assertTrue(movie.getManagers().contains(account));
    }

    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 개설 실패")
    void createMovie_failure() throws Exception {
        mockMvc.perform(post("/new-movie")
                .param("path", "wrong path")
                .param("title", "movie title")
                .param("shortDescription", "short description of a movie")
                .param("fullDescription", "full description of a movie")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("movie/form"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeExists("movieForm"))
                .andExpect(model().attributeExists("account"));

        Movie movie = movieRepository.findByPath("test-path");
        assertNull(movie);
    }

    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 조회")
    void viewMovie() throws  Exception {
        Movie movie = new Movie();
        movie.setPath("test-path");
        movie.setTitle("test movie");
        movie.setShortDescription("sd");
        movie.setFullDescription("<p>full description </p>");

        Account account = accountRepository.findByNickname("seungyeol");
        movieService.createNewMovie(movie,account);

        mockMvc.perform(get("/movie/test-path"))
                .andExpect(view().name("movie/view"))
                .andExpect(model().attributeExists("account"))
                .andExpect(model().attributeExists("movie"));

    }

    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 가입")
    void joinMovie() throws Exception {
        Account winwarm = createAccount("winwarm");

        Movie movie = createMovie("test-comu", winwarm);

        mockMvc.perform(get("/movie/" + movie.getPath() + "/join"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/" + movie.getPath() + "/members"));

        Account seungyeol = accountRepository.findByNickname("seungyeol");
        assertTrue(movie.getMembers().contains(seungyeol));
    }

    @Test
    @WithAccount("seungyeol")
    @DisplayName("모임 탈퇴")
    void leaveMovie() throws Exception {
        Account winwarm = createAccount("winwarm");

        Movie movie = createMovie("test-comu", winwarm);
        Account seungyeol = accountRepository.findByNickname("seungyeol");

        movieService.addMember(movie,seungyeol);

        mockMvc.perform(get("/movie/" + movie.getPath() + "/leave"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/" + movie.getPath() + "/members"));

        assertFalse(movie.getMembers().contains(seungyeol));
    }

    protected Account createAccount(String nickname) {
        Account winwarm = new Account();
        winwarm.setNickname(nickname);
        winwarm.setEmail(nickname + "@email.com");
        accountRepository.save(winwarm);
        return winwarm;
    }

    protected Movie createMovie(String path, Account manager) {
        Movie movie = new Movie();
        movie.setPath(path);
        movieService.createNewMovie(movie, manager);
        return movie;
    }
}