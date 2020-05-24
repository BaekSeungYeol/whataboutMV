package com.whataboutmv.modules.movie;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface MovieRepositoryExtension {

    Page<Movie> findByKeyword(String keyword, Pageable pageable);

}
