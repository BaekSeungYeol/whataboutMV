package com.whataboutmv.modules.movie;

import com.whataboutmv.modules.account.Account;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MovieFactory {

    @Autowired
    MovieService movieService;
    @Autowired MovieRepository movieRepository;

    public Movie createMovie(String path, Account manager) {
        Movie movie = new Movie();
        movie.setPath(path);
        movieService.createNewMovie(movie, manager);
        return movie;
    }

}