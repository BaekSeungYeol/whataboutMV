package com.whataboutmv.modules.movie.event;

import com.whataboutmv.modules.movie.Movie;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@Getter
@RequiredArgsConstructor
public class MovieUpdateEvent {

    private final Movie movie;

    private final String message;

}
