package com.teenteen.topping.user;

import com.teenteen.topping.user.VO.LikeList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeListRepository extends JpaRepository<LikeList,Long> {

}
