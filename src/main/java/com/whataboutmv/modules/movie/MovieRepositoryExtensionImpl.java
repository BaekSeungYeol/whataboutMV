package com.whataboutmv.modules.movie;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.whataboutmv.modules.account.QAccount;
import com.whataboutmv.modules.tag.QTag;
import com.whataboutmv.modules.zone.QZone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class MovieRepositoryExtensionImpl extends QuerydslRepositorySupport implements MovieRepositoryExtension{


    public MovieRepositoryExtensionImpl() {
        super(Movie.class);
    }

    @Override
    public Page<Movie> findByKeyword(String keyword, Pageable pageable) {
        QMovie movie = QMovie.movie;
        JPQLQuery<Movie> query = from(movie).where(movie.published.isTrue()
                .and(movie.title.containsIgnoreCase(keyword))
                .or(movie.tags.any().title.containsIgnoreCase(keyword))
                .or(movie.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(movie.tags, QTag.tag).fetchJoin()
                .leftJoin(movie.zones, QZone.zone).fetchJoin()
                .leftJoin(movie.members, QAccount.account).fetchJoin()
                .distinct();

        JPQLQuery<Movie> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Movie> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }
}
