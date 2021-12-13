package com.teenteen.topping.user;

import com.teenteen.topping.user.VO.SuspicionUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SuspicionUserRepository extends JpaRepository<SuspicionUser, Long> {

    @Query(value = "select count(u) from suspicion_user u where u.suspicionUserId = :suspicionUserId")
    Optional<Long> countReport(Long suspicionUserId);

}
