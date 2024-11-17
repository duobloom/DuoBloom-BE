package POT.DuoBloom.domain.community.repository;

import POT.DuoBloom.domain.community.entity.CommunityImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {
}
