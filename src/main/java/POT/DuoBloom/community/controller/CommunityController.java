package POT.DuoBloom.community.controller;

import POT.DuoBloom.community.dto.CommunityRequestDto;
import POT.DuoBloom.community.dto.CommunityResponseDto;
import POT.DuoBloom.community.service.CommunityService;
import POT.DuoBloom.user.UserRepository;
import POT.DuoBloom.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final UserRepository userRepository;

    @Operation(summary = "커뮤니티 게시글 생성", description = "새로운 커뮤니티 게시글을 생성합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @PostMapping
    public ResponseEntity<CommunityResponseDto> createCommunity(
            @RequestBody CommunityRequestDto requestDto,
            @SessionAttribute(name = "userId", required = false) Long userId,
            HttpSession session) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = getUserFromSessionOrDb(userId, session);

        CommunityResponseDto responseDto = communityService.createCommunity(requestDto, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "모든 커뮤니티 게시글 조회", description = "모든 커뮤니티 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 조회 성공")
    @GetMapping
    public ResponseEntity<List<CommunityResponseDto>> getAllCommunities(
            @SessionAttribute(name = "userId", required = false) Long userId) {

        List<CommunityResponseDto> responseDtos = communityService.getAllCommunities(userId);
        return ResponseEntity.ok(responseDtos);
    }

    @Operation(summary = "커뮤니티 게시글 수정", description = "특정 커뮤니티 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @PutMapping("/{communityId}")
    public ResponseEntity<CommunityResponseDto> updateCommunity(
            @PathVariable Integer communityId,
            @RequestBody CommunityRequestDto requestDto,
            @SessionAttribute(name = "userId", required = false) Long userId,
            HttpSession session) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = getUserFromSessionOrDb(userId, session);

        CommunityResponseDto responseDto = communityService.updateCommunity(communityId, requestDto, user.getUserId());
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "커뮤니티 게시글 삭제", description = "특정 커뮤니티 게시글을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @DeleteMapping("/{communityId}")
    public ResponseEntity<Void> deleteCommunity(
            @PathVariable Integer communityId,
            @SessionAttribute(name = "userId", required = false) Long userId,
            HttpSession session) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = getUserFromSessionOrDb(userId, session);

        communityService.deleteCommunity(communityId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    private User getUserFromSessionOrDb(Long userId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
            session.setAttribute("user", user);
        }
        return user;
    }
}
