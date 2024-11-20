package POT.DuoBloom.domain.community.repository;

import POT.DuoBloom.domain.community.entity.CommunityScrap;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CommunityScrapRepository extends JpaRepository<CommunityScrap, Long> {
    boolean existsByCommunity_CommunityIdAndUser_UserId(Long communityId, Long userId);

    //List<CommunityScrap> findByUser_UserId(Long userId);
    //Optional<CommunityScrap> findByCommunity_CommunityIdAndUser_UserId(Long communityId, Long userId);

    @Query("SELECT cs FROM CommunityScrap cs " +
            "JOIN FETCH cs.community c " +
            "JOIN FETCH cs.user u " +
            "WHERE u.userId = :userId")
    List<CommunityScrap> findByUser_UserId(@Param("userId") Long userId);

    @Query("SELECT cs FROM CommunityScrap cs " +
            "JOIN FETCH cs.community c " +
            "JOIN FETCH cs.user u " +
            "WHERE c.communityId = :communityId AND u.userId = :userId")
    Optional<CommunityScrap> findByCommunity_CommunityIdAndUser_UserId(
            @Param("communityId") Long communityId,
            @Param("userId") Long userId
    );

}
