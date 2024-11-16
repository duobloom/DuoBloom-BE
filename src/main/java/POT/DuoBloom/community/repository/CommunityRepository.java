package POT.DuoBloom.community.repository;

import POT.DuoBloom.community.dto.CommunityResponseDto;
import POT.DuoBloom.community.entity.Community;
import POT.DuoBloom.community.entity.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT c.communityId FROM Community c WHERE (:type IS NULL OR c.type = :type)")
    List<Long> findCommunityIdsByType(@Param("type") Type type);
}


