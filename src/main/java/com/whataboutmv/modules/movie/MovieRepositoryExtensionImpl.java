package com.whataboutmv.modules.movie;

import com.querydsl.jpa.JPQLQuery;
import com.whataboutmv.modules.account.QAccount;
import com.whataboutmv.modules.tag.QTag;
import com.whataboutmv.modules.zone.QZone;
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
                .or(movie.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(movie.tags, QTag.tag).fetchJoin()
                .leftJoin(movie.zones, QZone.zone).fetchJoin()
                .leftJoin(movie.members, QAccount.account).fetchJoin()
                .distinct();
        return query.fetch();
    }
}
