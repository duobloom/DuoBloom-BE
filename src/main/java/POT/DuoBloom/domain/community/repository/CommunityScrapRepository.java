package POT.DuoBloom.domain.community.repository;

import POT.DuoBloom.domain.community.entity.CommunityScrap;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityScrapRepository extends JpaRepository<CommunityScrap, Long> {
    boolean existsByCommunity_CommunityIdAndUser_UserId(Long communityId, Long userId);

    List<CommunityScrap> findByUser_UserId(Long userId);
    Optional<CommunityScrap> findByCommunity_CommunityIdAndUser_UserId(Long communityId, Long userId);

}
