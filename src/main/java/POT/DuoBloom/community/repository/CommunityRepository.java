package POT.DuoBloom.community.repository;

import POT.DuoBloom.community.entity.Community;
import POT.DuoBloom.community.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;


import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT c.communityId FROM Community c WHERE (:type IS NULL OR c.type = :type)")
    List<Long> findCommunityIdsByType(@Param("type") Type type);

    @Query("SELECT c FROM Community c " +
            "LEFT JOIN CommunityLike cl ON cl.community = c " +
            "WHERE c.type = :type " +
            "GROUP BY c " +
            "ORDER BY COUNT(cl) DESC, c.communityId DESC")
    List<Community> findTopByType(@Param("type") Type type, Pageable pageable);

    List<Community> findByType(Type type);


}


