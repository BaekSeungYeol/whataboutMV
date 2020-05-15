package com.whataboutmv.modules.movie;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface MovieRepositoryExtension {

    List<Movie> findByKeyword(String keyword);

}
