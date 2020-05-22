package com.whataboutmv.modules.movie;

import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class MovieRepositoryExtensionImpl extends QuerydslRepositorySupport implements MovieRepositoryExtension{


    public MovieRepositoryExtensionImpl() {
        super(Movie.class);
    }

    @Override
    public List<Movie> findByKeyword(String keyword) {
        QMovie movie = QMovie.movie;
        JPQLQuery<Movie> query = from(movie).where(movie.published.isTrue()
                .and(movie.title.containsIgnoreCase(keyword))
                .or(movie.tags.any().title.containsIgnoreCase(keyword))
                .or(movie.zones.any().localNameOfCity.containsIgnoreCase(keyword)));

        return query.fetch();
    }
}
