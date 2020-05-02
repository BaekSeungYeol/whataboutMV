package com.whataboutmv.movie;

import com.whataboutmv.domain.Account;
import com.whataboutmv.domain.Movie;
import com.whataboutmv.movie.form.MovieDescriptionForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;

    public Movie createNewMovie(Movie movie, Account account) {
        Movie savedMovie = movieRepository.save(movie);
        savedMovie.addManager(account);
        return savedMovie;
    }

    public Movie getMovieToUpdate(Account account, String path) {
        Movie movie = this.getMovie(path);
        if(!account.isManagerOf(movie)){
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }

        return movie;
    }

    public Movie getMovie(String path) {
        Movie movie = this.movieRepository.findByPath(path);
        if(movie == null) {
            throw new IllegalArgumentException(path + "에 해당하는 모임이 없습니다.");
        }
        return movie;
    }

    public void updateMovieDescription(Movie movie, MovieDescriptionForm movieDescriptionForm) {
        modelMapper.map(movieDescriptionForm, movie);
    }

    public void updateMovieImage(Movie movie, String image) {
        movie.setImage(image);
    }

    public void enableMovieBanner(Movie movie) {
        movie.setUseBanner(true);
    }
    public void disableMovieBanner(Movie movie) {
        movie.setUseBanner(false);
    }


}
