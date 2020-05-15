package com.whataboutmv.modules.movie.event;

import com.whataboutmv.modules.movie.Movie;
import lombok.Getter;

@Getter
public class MovieCreatedEvent {

    private Movie movie;

    public MovieCreatedEvent(Movie movie) {
       this.movie = movie;
    }
}
