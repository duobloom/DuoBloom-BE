package POT.DuoBloom.domain.community.repository;

import POT.DuoBloom.domain.community.entity.Community;
import POT.DuoBloom.domain.community.entity.CommunityLike;
import POT.DuoBloom.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    Optional<CommunityLike> findByUserAndCommunity(User user, Community community);
    long countByCommunity(Community community);
}
