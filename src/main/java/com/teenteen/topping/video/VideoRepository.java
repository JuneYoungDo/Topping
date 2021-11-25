package com.teenteen.topping.video;

import com.teenteen.topping.video.VO.Video;
import com.teenteen.topping.video.VideoDto.UserVideoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query(value = "select v from Video v where v.deleted = false and v.user.userId = :userId " +
            "order by v.createdAt DESC")
    Optional<List<UserVideoList>> getVideosByUserId(Long userId);
}
