package com.teenteen.topping.user;

import com.teenteen.topping.user.UserDto.UserProfileRes;
import com.teenteen.topping.user.VO.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<UserProfileRes> findByUserId(Long userId);

    @Query(value = "select l.mode from like_list l where l.user.userId = :userId and l.video.videoId = :videoId")
    Optional<Long> existedReact(Long userId, Long videoId);

    @Modifying
    @Query(value = "update like_list l set l.mode = :mode " +
            "where l.user.userId = :userId and l.video.videoId = :videoId")
    int editReact(Long userId, Long videoId, Long mode);

    void deleteUserByUserId(Long userId);

}
