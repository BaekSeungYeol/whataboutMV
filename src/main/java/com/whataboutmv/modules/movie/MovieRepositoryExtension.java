package com.whataboutmv.modules.movie;

import com.whataboutmv.modules.tag.Tag;
import com.whataboutmv.modules.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface MovieRepositoryExtension {

    Page<Movie> findByKeyword(String keyword, Pageable pageable);
    List<Movie> findByAccount(Set<Tag> tags, Set<Zone> zones);
}
