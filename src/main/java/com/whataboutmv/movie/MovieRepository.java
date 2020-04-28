package com.whataboutmv.movie;

import com.whataboutmv.domain.Movie;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MovieRepository extends JpaRepository<Movie,Long> {

    boolean existsByPath(String path);

    @EntityGraph(value = "Movie.withAll", type= EntityGraph.EntityGraphType.LOAD)
    Movie findByPath(String path);
}
