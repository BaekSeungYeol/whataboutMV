package com.whataboutmv.modules.event;

import com.whataboutmv.infra.AbstractContainerBaseTest;
import com.whataboutmv.infra.MockMvcTest;
import com.whataboutmv.modules.account.AccountFactory;
import com.whataboutmv.modules.account.AccountRepository;
import com.whataboutmv.modules.account.WithAccount;
import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.movie.Movie;
import com.whataboutmv.modules.movie.MovieControllerTest;
import com.whataboutmv.modules.movie.MovieFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockMvcTest
class EventControllerTest {

    @Autowired
    EventService eventService;
    @Autowired EnrollmentRepository enrollmentRepository;
    @Autowired
    AccountFactory accountFactory;
    @Autowired
    MovieFactory movieFactory;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 자동 수락")
    @WithAccount("seungyeol")
    void newEnrollment_to_FCFS_event_accepted() throws Exception {
        Account winwarm = accountFactory.createAccount("winwarm");
        Movie movie = movieFactory.createMovie("test-movie", winwarm);
        Event event = createEvent("test-event", EventType.FCFS, 2, movie, winwarm);

        mockMvc.perform(post("/movie/" + movie.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/" + movie.getPath() + "/events/" + event.getId()));

        Account seungyeol = accountRepository.findByNickname("seungyeol");
        isAccepted(seungyeol, event);
    }

    @Test
    @DisplayName("선착순 모임에 참가 신청 - 대기중 (이미 인원이 꽉차서)")
    @WithAccount("seungyeol")
    void newEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account winwarm = accountFactory.createAccount("winwarm");
        Movie movie = movieFactory.createMovie("test-movie", winwarm);
        Event event = createEvent("test-event", EventType.FCFS, 2, movie, winwarm);

        Account may = accountFactory.createAccount("may");
        Account june = accountFactory.createAccount("june");
        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, june);

        mockMvc.perform(post("/movie/" + movie.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/" + movie.getPath() + "/events/" + event.getId()));

        Account seungyeol = accountRepository.findByNickname("seungyeol");
        isNotAccepted(seungyeol, event);
    }

    @Test
    @DisplayName("참가신청 확정자가 선착순 모임에 참가 신청을 취소하는 경우, 바로 다음 대기자를 자동으로 신청 확인한다.")
    @WithAccount("seungyeol")
    void accepted_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account seungyeol = accountRepository.findByNickname("seungyeol");
        Account winwarm = accountFactory.createAccount("winwarm");
        Account may = accountFactory.createAccount("may");
        Movie movie = movieFactory.createMovie("test-movie", winwarm);
        Event event = createEvent("test-event", EventType.FCFS, 2, movie, winwarm);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, seungyeol);
        eventService.newEnrollment(event, winwarm);

        isAccepted(may, event);
        isAccepted(seungyeol, event);
        isNotAccepted(winwarm, event);

        mockMvc.perform(post("/movie/" + movie.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/" + movie.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(winwarm, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, seungyeol));
    }

    @Test
    @DisplayName("참가신청 비확정자가 선착순 모임에 참가 신청을 취소하는 경우, 기존 확정자를 그대로 유지하고 새로운 확정자는 없다.")
    @WithAccount("seungyeol")
    void not_accepterd_account_cancelEnrollment_to_FCFS_event_not_accepted() throws Exception {
        Account seungyeol = accountRepository.findByNickname("seungyeol");
        Account winwarm = accountFactory.createAccount("winwarm");
        Account may = accountFactory.createAccount("may");
        Movie movie = movieFactory.createMovie("test-movie", winwarm);
        Event event = createEvent("test-event", EventType.FCFS, 2, movie, winwarm);

        eventService.newEnrollment(event, may);
        eventService.newEnrollment(event, winwarm);
        eventService.newEnrollment(event, seungyeol);

        isAccepted(may, event);
        isAccepted(winwarm, event);
        isNotAccepted(seungyeol, event);

        mockMvc.perform(post("/movie/" + movie.getPath() + "/events/" + event.getId() + "/disenroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/" + movie.getPath() + "/events/" + event.getId()));

        isAccepted(may, event);
        isAccepted(winwarm, event);
        assertNull(enrollmentRepository.findByEventAndAccount(event, seungyeol));
    }

    private void isNotAccepted(Account winwarm, Event event) {
        assertFalse(enrollmentRepository.findByEventAndAccount(event, winwarm).isAccepted());
    }

    private void isAccepted(Account account, Event event) {
        assertTrue(enrollmentRepository.findByEventAndAccount(event, account).isAccepted());
    }

    @Test
    @DisplayName("관리자 확인 모임에 참가 신청 - 대기중")
    @WithAccount("seungyeol")
    void newEnrollment_to_CONFIMATIVE_event_not_accepted() throws Exception {
        Account winwarm = accountFactory.createAccount("winwarm");
        Movie movie = movieFactory.createMovie("test-movie", winwarm);
        Event event = createEvent("test-event", EventType.CONFIRMATIVE, 2, movie, winwarm);

        mockMvc.perform(post("/movie/" + movie.getPath() + "/events/" + event.getId() + "/enroll")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/movie/" + movie.getPath() + "/events/" + event.getId()));

        Account seungyeol = accountRepository.findByNickname("seungyeol");
        isNotAccepted(seungyeol, event);
    }

    private Event createEvent(String eventTitle, EventType eventType, int limit, Movie movie, Account account) {
        Event event = new Event();
        event.setEventType(eventType);
        event.setLimitOfEnrollments(limit);
        event.setTitle(eventTitle);
        event.setCreatedDateTime(LocalDateTime.now());
        event.setEndEnrollmentDateTime(LocalDateTime.now().plusDays(1));
        event.setStartDateTime(LocalDateTime.now().plusDays(1).plusHours(5));
        event.setEndDateTime(LocalDateTime.now().plusDays(1).plusHours(7));
        return eventService.createEvent(event, movie, account);
    }

}