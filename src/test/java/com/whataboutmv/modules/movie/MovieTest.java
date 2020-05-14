package com.whataboutmv.modules.movie;

import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.account.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    Movie movie;
    Account account;
    UserAccount userAccount;

    @BeforeEach
    void beforeEach() {
        movie = new Movie();
        account = new Account();
        account.setNickname("keesun");
        account.setPassword("123");
        userAccount = new UserAccount(account);

    }

    @DisplayName("영화모임를 공개했고 인원 모집 중이고, 이미 멤버나 영화모임 관리자가 아니라면 영화모임 가입 가능")
    @Test
    void isJoinable() {
        movie.setPublished(true);
        movie.setRecruiting(true);

        assertTrue(movie.isJoinable(userAccount));
    }

    @DisplayName("영화모임를 공개했고 인원 모집 중이더라도, 영화모임 관리자는 영화모임 가입이 불필요하다.")
    @Test
    void isJoinable_false_for_manager() {
        movie.setPublished(true);
        movie.setRecruiting(true);
        movie.addManager(account);

        assertFalse(movie.isJoinable(userAccount));
    }

    @DisplayName("영화모임를 공개했고 인원 모집 중이더라도, 영화모임 멤버는 영화모임 재가입이 불필요하다.")
    @Test
    void isJoinable_false_for_member() {
        movie.setPublished(true);
        movie.setRecruiting(true);
        movie.addMember(account);

        assertFalse(movie.isJoinable(userAccount));
    }

    @DisplayName("영화모임가 비공개거나 인원 모집 중이 아니면 영화모임 가입이 불가능하다.")
    @Test
    void isJoinable_false_for_non_recruiting_movie() {
        movie.setPublished(true);
        movie.setRecruiting(false);

        assertFalse(movie.isJoinable(userAccount));

        movie.setPublished(false);
        movie.setRecruiting(true);

        assertFalse(movie.isJoinable(userAccount));
    }

    @DisplayName("영화모임 관리자인지 확인")
    @Test
    void isManager() {
        movie.addManager(account);
        assertTrue(movie.isManager(userAccount));
    }

    @DisplayName("영화모임 멤버인지 확인")
    @Test
    void isMember() {
        movie.addMember(account);
        assertTrue(movie.isMember(userAccount));
    }

}