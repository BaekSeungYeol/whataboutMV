package com.whataboutmv.modules.movie.event;

import com.whataboutmv.modules.movie.Movie;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Async
@Component
@Transactional(readOnly = true)
public class MovieEventListener {

    @EventListener
    public void handleMovieCreateEvent(MovieCreatedEvent movieCreatedEvent) {
        Movie movie = movieCreatedEvent.getMovie();
        log.info(movie.getTitle() + "is created.");
        throw new RuntimeException();
    }
}
