package com.whataboutmv.modules.movie;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import com.whataboutmv.modules.account.QAccount;
import com.whataboutmv.modules.tag.QTag;
import com.whataboutmv.modules.tag.Tag;
import com.whataboutmv.modules.zone.QZone;
import com.whataboutmv.modules.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

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
                .distinct();

        JPQLQuery<Movie> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Movie> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public List<Movie> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        QMovie movie = QMovie.movie;
        JPQLQuery<Movie> query = from(movie).where(movie.published.isTrue()
                .and(movie.closed.isFalse())
                .and(movie.tags.any().in(tags))
                .and(movie.zones.any().in(zones)))
                .leftJoin(movie.tags, QTag.tag).fetchJoin()
                .leftJoin(movie.zones, QZone.zone).fetchJoin()
                .orderBy(movie.publishedDateTime.desc())
                .distinct()
                .limit(9);
        return query.fetch();
    }
}
