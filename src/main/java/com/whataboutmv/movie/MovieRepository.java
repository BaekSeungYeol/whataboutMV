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

    //WithTags는 무시 키워드
    @EntityGraph(value = "Movie.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Movie findMovieWithTagsByPath(String path);

    @EntityGraph(value = "Movie.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Movie findMovieWithZonesByPath(String path);
}
