package com.whataboutmv.modules.movie.event;

import com.whataboutmv.modules.movie.Movie;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
public class MovieCreatedEvent {

    private final Movie movie;

    public MovieCreatedEvent(Movie movie) {
       this.movie = movie;
    }
}
