package com.teenteen.topping.user;

import com.teenteen.topping.user.VO.LikeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeListRepository extends JpaRepository<LikeList,Long> {
    @Query(value = "select count(l) from like_list l " +
            "where l.mode = 1 and l.video.videoId = :videoId and l.video.deleted = false")
    Optional<Long> countGood(Long videoId);

    @Query(value = "select count(l) from like_list l " +
            "where l.mode = 2 and l.video.videoId = :videoId and l.video.deleted = false")
    Optional<Long> countFire(Long videoId);

    @Query(value = "select count(l) from like_list l " +
            "where l.mode = 3 and l.video.videoId = :videoId and l.video.deleted = false")
    Optional<Long> countFace(Long videoId);

    @Query(value = "select l.mode from like_list l " +
            "where l.user.userId = :userId and l.video.videoId = :videoId and l.video.deleted = false")
    Optional<Long> getMode(Long userId, Long videoId);
}
