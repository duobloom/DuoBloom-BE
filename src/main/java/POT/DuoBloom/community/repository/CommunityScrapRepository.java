package POT.DuoBloom.community.repository;

import POT.DuoBloom.community.entity.Community;
import POT.DuoBloom.community.entity.CommunityScrap;
import POT.DuoBloom.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityScrapRepository extends JpaRepository<CommunityScrap, Long> {
    List<CommunityScrap> findByUser(User user);
    Optional<CommunityScrap> findByUserAndCommunity(User user, Community community);
}
