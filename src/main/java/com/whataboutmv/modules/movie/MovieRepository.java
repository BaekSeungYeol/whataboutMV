package com.whataboutmv.modules.movie;

import com.whataboutmv.modules.account.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface MovieRepository extends JpaRepository<Movie,Long>, MovieRepositoryExtension {

    boolean existsByPath(String path);

    @EntityGraph(value = "Movie.withAll", type= EntityGraph.EntityGraphType.LOAD)
    Movie findByPath(String path);

    //WithTags는 무시 키워드
    @EntityGraph(value = "Movie.withTagsAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Movie findMovieWithTagsByPath(String path);

    @EntityGraph(value = "Movie.withZonesAndManagers", type = EntityGraph.EntityGraphType.FETCH)
    Movie findMovieWithZonesByPath(String path);

    @EntityGraph(value = "Movie.withManagers", type = EntityGraph.EntityGraphType.FETCH)
    Movie findMovieWithManagersByPath(String path);

    boolean existsByTitle(String newTitle);

    @EntityGraph(value = "Movie.withMembers", type = EntityGraph.EntityGraphType.FETCH)
    Movie findMovieWithMembersByPath(String path);

    Movie findMovieOnlyByPath(String path);

    @EntityGraph(value = "Movie.withTagsAndZones", type = EntityGraph.EntityGraphType.FETCH)
    Movie findMovieWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"})
    Movie findMovieWithManagersAndMembersById(Long id);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Movie> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    List<Movie> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Movie> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);
}
