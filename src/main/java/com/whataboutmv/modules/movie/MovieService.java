package com.whataboutmv.modules.movie;

import com.whataboutmv.modules.account.Account;
import com.whataboutmv.modules.movie.event.MovieCreatedEvent;
import com.whataboutmv.modules.movie.event.MovieUpdateEvent;
import com.whataboutmv.modules.tag.Tag;
import com.whataboutmv.modules.tag.TagRepository;
import com.whataboutmv.modules.zone.Zone;
import com.whataboutmv.modules.movie.form.MovieDescriptionForm;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

import static com.whataboutmv.modules.movie.form.MovieForm.VALID_PATH_PATTERN;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final TagRepository tagRepository;


    public Movie createNewMovie(Movie movie, Account account) {
        Movie newMovie = movieRepository.save(movie);
        newMovie.addManager(account);
        return newMovie;
    }

    public Movie getMovieToUpdate(Account account, String path) {
        Movie movie = this.getMovie(path);
        if(!movie.isManagedBy(account)) {
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
        eventPublisher.publishEvent(new MovieUpdateEvent(movie, "영화모임 소개를 수정했습니다."));
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

    public void addTag(Movie movie, Tag tag) {
        movie.getTags().add(tag);
    }
    public void removeTag(Movie movie, Tag tag) {
        movie.getTags().remove(tag);
    }

    public void addZone(Movie movie, Zone zone) {
        movie.getZones().add(zone);
    }

    public void removeZone(Movie movie, Zone zone) {
        movie.getZones().remove(zone);
    }

    public Movie getMovieToUpdateTag(Account account, String path) {
        Movie movie = movieRepository.findMovieWithTagsByPath(path);
        checkIfExistingMovie(path,movie);
        checkIfManager(account, movie);
        return movie;
    }

    private void checkIfManager(Account account, Movie movie) {
        if(!movie.isManagedBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    private void checkIfExistingMovie(String path, Movie movie) {
        if(movie == null) {
            throw new IllegalArgumentException(path + "에 해당하는 모임이 없습니다.");
        }
    }


    public Movie getMovieToUpdateZone(Account account, String path) {
        Movie movie = movieRepository.findMovieWithZonesByPath(path);
        checkIfExistingMovie(path,movie);
        checkIfManager(account, movie);
        return movie;
    }

    public Movie getMovieToUpdateStatus(Account account, String path) {
        Movie movie = movieRepository.findMovieWithManagersByPath(path);
        checkIfExistingMovie(path,movie);
        checkIfManager(account, movie);
        return movie;
    }

    public void publish(Movie movie){
        movie.publish();
        this.eventPublisher.publishEvent(new MovieCreatedEvent(movie));
    }

    public void close(Movie movie) {
        movie.close();
        eventPublisher.publishEvent(new MovieUpdateEvent(movie, "영화모임을 종료했습니다."));
    }

    public void startRecruit(Movie movie) {
        movie.startRecruit();
        eventPublisher.publishEvent(new MovieUpdateEvent(movie, "영화모임을 시작합니다."));

    }

    public void stopRecruit(Movie movie) {
        movie.stopRecruit();
        eventPublisher.publishEvent(new MovieUpdateEvent(movie, "영화모임을 중단헀습니다."));

    }

    public boolean isValidPath(String newPath) {
        if(!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }
        return !movieRepository.existsByPath(newPath);
    }

    public void updateMoviePath(Movie movie, String newPath) {
        movie.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateMovieTitle(Movie movie, String newTitle) {
        movie.setTitle(newTitle);
    }

    public void remove(Movie movie) {
        if(movie.isRemovable()) {
            movieRepository.delete(movie);
        } else {
            throw new IllegalArgumentException("모임을 삭제할 수 없습니다.");
        }
    }

    public void addMember(Movie movie, Account account) {
        movie.addMember(account);
    }

    public void removeMember(Movie movie, Account account) {
        movie.removeMember(account);
    }

    public Movie getMovieToEnroll(String path) {
        Movie movie = movieRepository.findMovieOnlyByPath(path);
        checkIfExistingMovie(path, movie);

        return movie;
    }

    public void generateTestMovies(Account account) {
        for(int i=0; i<30; ++i) {
            String randomValue = RandomString.make(5);
            Movie testMovie = Movie.builder()
                    .title("테스트 영화모임" + randomValue)
                    .path("test-" + randomValue)
                    .shortDescription("테스트 모임")
                    .tags(new HashSet<>())
                    .managers(new HashSet<>())
                    .build();

            testMovie.publish();
            Movie newMovie = this.createNewMovie(testMovie, account);
            Tag crime = tagRepository.findByTitle("스릴러");
            newMovie.getTags().add(crime);
        }
    }
}

