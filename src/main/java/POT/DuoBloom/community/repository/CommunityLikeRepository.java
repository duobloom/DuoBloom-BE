package POT.DuoBloom.community.repository;

import POT.DuoBloom.community.entity.Community;
import POT.DuoBloom.community.entity.CommunityLike;
import POT.DuoBloom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {
    Optional<CommunityLike> findByUserAndCommunity(User user, Community community);
    long countByCommunity(Community community);
}
