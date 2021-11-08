package com.teenteen.topping.challenge;


import com.teenteen.topping.challenge.VO.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge,Long> {
    Optional<Challenge> existsChallengeByChallengeIdAndDeleted(Long challengeId, boolean b);
}