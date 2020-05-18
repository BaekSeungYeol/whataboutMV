package com.whataboutmv.modules.movie.event;

import com.whataboutmv.modules.account.AccountRepository;
import com.whataboutmv.modules.movie.Movie;
import com.whataboutmv.modules.movie.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional
@RequiredArgsConstructor
public class MovieEventListener {

    private final MovieRepository movieRepository;
    private final AccountRepository accountRepository;

    @EventListener
    public void handleMovieCreateEvent(MovieCreatedEvent movieCreatedEvent) {
        Movie movie = movieRepository.findMovieWithTagsAndZonesById(movieCreatedEvent.getMovie().getId());


    }
}
