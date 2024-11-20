package POT.DuoBloom.domain.community.repository;

import POT.DuoBloom.domain.community.entity.Community;
import POT.DuoBloom.domain.community.entity.CommunityLike;
import POT.DuoBloom.domain.user.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    //Optional<CommunityLike> findByUserAndCommunity(User user, Community community);
    @Query("SELECT cl FROM CommunityLike cl " +
            "JOIN FETCH cl.user u " +
            "JOIN FETCH cl.community c " +
            "WHERE u = :user AND c = :community")
    Optional<CommunityLike> findByUserAndCommunity(
            @Param("user") User user,
            @Param("community") Community community
    );

    //long countByCommunity(Community community);
    @Query("SELECT COUNT(cl) FROM CommunityLike cl WHERE cl.community = :community")
    long countByCommunity(@Param("community") Community community);

}
