package POT.DuoBloom.domain.community.controller;

import POT.DuoBloom.domain.community.dto.response.CommunityListResponseDto;
import POT.DuoBloom.domain.community.service.CommunityScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/community-scrap")
public class CommunityScrapController {

    private final CommunityScrapService scrapService;

    @PostMapping("/{communityId}")
    public ResponseEntity<Void> scrapCommunity(@PathVariable Long communityId, @SessionAttribute Long userId) {
        scrapService.scrapCommunity(communityId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{communityId}")
    public ResponseEntity<Void> unscrapCommunity(@PathVariable Long communityId, @SessionAttribute Long userId) {
        scrapService.unscrapCommunity(communityId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<CommunityListResponseDto>> getScrappedCommunities(@SessionAttribute Long userId) {
        List<CommunityListResponseDto> scrappedCommunities = scrapService.getScrappedCommunities(userId);
        return ResponseEntity.ok(scrappedCommunities);
    }
}
