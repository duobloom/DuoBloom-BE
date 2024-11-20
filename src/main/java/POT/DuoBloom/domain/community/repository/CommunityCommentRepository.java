package POT.DuoBloom.domain.community.repository;

import POT.DuoBloom.domain.community.entity.Community;
import POT.DuoBloom.domain.community.entity.CommunityComment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {
    //List<CommunityComment> findByCommunity(Community community);
    @Query("SELECT cc FROM CommunityComment cc " +
            "JOIN FETCH cc.user u " +
            "JOIN FETCH cc.community c " +
            "WHERE c = :community")
    List<CommunityComment> findByCommunity(@Param("community") Community community);

    long countByCommunity(Community community);
}
