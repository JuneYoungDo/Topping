package com.teenteen.topping.challenge;


import com.teenteen.topping.challenge.VO.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge,Long> {
    Optional<List<Challenge>> findAllByCategory(Long CategoryId);
}