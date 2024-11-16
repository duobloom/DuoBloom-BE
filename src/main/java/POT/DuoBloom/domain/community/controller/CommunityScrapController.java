package POT.DuoBloom.domain.community.controller;

import POT.DuoBloom.domain.community.dto.response.CommunityListResponseDto;
import POT.DuoBloom.domain.community.service.CommunityScrapService;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.service.UserService;
import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community-scrap")
@RequiredArgsConstructor
public class CommunityScrapController {

    private final CommunityScrapService communityScrapService;
    private final UserService userService;

    @PostMapping("/{communityId}")
    public ResponseEntity<Void> scrapCommunity(
            @PathVariable Long communityId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        User user = userService.findById(userId);
        communityScrapService.scrapCommunity(user, communityId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<CommunityListResponseDto>> getScrappedCommunities(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        User user = userService.findById(userId);
        List<CommunityListResponseDto> scrappedCommunities = communityScrapService.getScrappedCommunities(user);
        return ResponseEntity.ok(scrappedCommunities);
    }

    /**
     * 커뮤니티 게시글 스크랩 취소
     */
    @DeleteMapping("/{communityId}")
    public ResponseEntity<Void> unsaveCommunity(
            @PathVariable Long communityId,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        User user = userService.findById(userId);
        communityScrapService.unsaveCommunity(user, communityId);
        return ResponseEntity.noContent().build();
    }
}
