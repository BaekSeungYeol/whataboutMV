package com.whataboutmv.movie;

import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Movie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    public Movie createNewMovie(Movie movie, Account account) {
        Movie savedMovie = movieRepository.save(movie);
        savedMovie.addManager(account);
        return savedMovie;
    }
}
