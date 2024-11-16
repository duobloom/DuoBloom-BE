package POT.DuoBloom.community.repository;

import POT.DuoBloom.community.entity.Community;
import POT.DuoBloom.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    List<CommunityComment> findByCommunity(Community community);
    long countByCommunity(Community community);
}
