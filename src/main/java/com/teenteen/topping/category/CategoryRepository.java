package com.teenteen.topping.category;

import com.teenteen.topping.category.CategoryDto.CategoryListRes;
import com.teenteen.topping.category.VO.Category;
import com.teenteen.topping.challenge.ChallengeDto.SimpleSearchRes;
import com.teenteen.topping.challenge.VO.Challenge;
import com.teenteen.topping.video.VO.Video;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<List<CategoryListRes>> findByDeleted(boolean b);

    boolean existsByCategoryId(Long categoryId);

    @Query(value = "select distinct v from Video v where v.challenge.category = :category " +
            "and v.challenge.deleted = false and v.deleted = false order by FUNCTION('RAND')")
    Optional<List<Video>> getVideoByCategory(Category category, Pageable limit);

    @Query(value = "select c from Challenge c where c.category in :categoryList " +
            "and c.deleted = false order by function('RAND') ")
    Optional<List<Challenge>> getChallengesByCategory(List<Category> categoryList);

    @Query(value = "select v from Video v where v.challenge = :challenge and v.deleted = false order by v.createdAt DESC")
    Optional<List<Video>> getRecentVideoByChallenge(Challenge challenge, Pageable limit);

    @Query(value = "select c from Challenge c " +
            "where c.deleted = false and c.category.categoryId = :categoryId order by c.createdAt DESC")
    Optional<List<Challenge>> getChallengesByCategorySortWithTime(Long categoryId);

    @Query(value = "select c from Challenge c " +
            "where c.deleted = false and c.category.categoryId = :categoryId order by c.viewCount DESC")
    Optional<List<Challenge>> getChallengesByCategorySortWithViewCount(Long categoryId);
}
