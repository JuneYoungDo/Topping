package com.teenteen.topping.video;

import com.teenteen.topping.video.VO.SuspicionVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspicionVideoRepository extends JpaRepository<SuspicionVideo, Long> {

    @Query(value = "select count(v) from suspicion_video v where v.videoId = :videoId")
    Optional<Long> countReport(Long videoId);

}
