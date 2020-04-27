package com.whataboutmv.movie;

import com.whataboutmv.domain.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface MovieRepository extends JpaRepository<Movie,Long> {

    boolean existsByPath(String path);
}
