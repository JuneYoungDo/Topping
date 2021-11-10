package com.teenteen.topping.challenge;


import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.ChallengeDto.SearchChallengeRes;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.video.VO.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    Optional<Challenge> existsChallengeByChallengeIdAndDeleted(Long challengeId, boolean b);

    @Query(value = "select c from Challenge c where c.deleted = false and c.name like %:searchWord%")
    Optional<List<SearchChallengeRes>> searchChallenge(String searchWord);

    @Query(value = "select distinct v from Video v where v.challenge.challengeId = :challengeId " +
            "and v.deleted = false order by v.createdAt desc")
    Optional<List<Video>> getVideoByChallenge(Long challengeId, Pageable limit);

    boolean existsByChallengeId(Long challengeId);

}