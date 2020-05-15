package com.whataboutmv.modules.movie;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class MovieRepositoryExtensionImpl extends QuerydslRepositorySupport implements MovieRepositoryExtension{


    public MovieRepositoryExtensionImpl() {
        super(Movie.class);
    }

    @Override
    public List<Movie> findByKeyword(String keyword) {
        return null;
    }
}
