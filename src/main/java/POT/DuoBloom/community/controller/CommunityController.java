package POT.DuoBloom.community.controller;

import POT.DuoBloom.community.dto.request.CommunityCommentRequestDto;
import POT.DuoBloom.community.dto.request.CommunityRequestDto;
import POT.DuoBloom.community.dto.response.CommunityCommentResponseDto;
import POT.DuoBloom.community.dto.response.CommunityFullResponseDto;
import POT.DuoBloom.community.dto.response.CommunityListResponseDto;
import POT.DuoBloom.community.dto.response.CommunityResponseDto;
import POT.DuoBloom.community.entity.Type;
import POT.DuoBloom.community.service.CommunityCommentService;
import POT.DuoBloom.community.service.CommunityService;
import POT.DuoBloom.user.repository.UserRepository;
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
    private final CommunityCommentService communityCommentService;
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

    @Operation(summary = "단일 게시글 조회", description = "단일 게시글을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "게시글 조회 성공")
    @GetMapping("/{communityId}")
    public ResponseEntity<CommunityFullResponseDto> getCommunityWithDetails(
            @PathVariable Long communityId,
            @SessionAttribute(name = "userId", required = false) Long userId) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CommunityFullResponseDto response = communityService.getCommunityWithDetails(communityId, userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "타입별 인기 게시글 조회", description = "모든 타입에 대해 좋아요가 가장 많은 게시글 상위 2개를 반환합니다.")
    @GetMapping("/top-by-type")
    public ResponseEntity<List<CommunityListResponseDto>> getTopCommunitiesByType(
            @SessionAttribute(name = "userId", required = false) Long userId
    ) {
        List<CommunityListResponseDto> response = communityService.getTopCommunitiesByType(userId);
        return ResponseEntity.ok(response);
    }


//    @Operation(summary = "커뮤니티 게시글 목록 조회", description = "모든 커뮤니티 게시글을 조회합니다.")
//    @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
//    @GetMapping
//    public ResponseEntity<List<CommunityListResponseDto>> getCommunityList(
//            @SessionAttribute(name = "userId", required = false) Long userId) {
//
//        if (userId == null) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        }
//
//        List<CommunityListResponseDto> responseDtos = communityService.getCommunityList(userId);
//        return ResponseEntity.ok(responseDtos);
//    }


    @Operation(summary = "커뮤니티 게시글 수정", description = "특정 커뮤니티 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @PutMapping("/{communityId}")
    public ResponseEntity<CommunityResponseDto> updateCommunity(
            @PathVariable Long communityId,
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
            @PathVariable Long communityId,
            @SessionAttribute(name = "userId", required = false) Long userId,
            HttpSession session) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = getUserFromSessionOrDb(userId, session);

        communityService.deleteCommunity(communityId, user.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "커뮤니티 게시글 좋아요 토글", description = "특정 커뮤니티 게시글에 좋아요를 누르거나 취소합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 토글 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "게시글 또는 사용자를 찾을 수 없습니다.")
    })
    @PostMapping("/{communityId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long communityId,
            @SessionAttribute(name = "userId", required = false) Long userId,
            HttpSession session) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = getUserFromSessionOrDb(userId, session);

        communityService.toggleLike(communityId, user.getUserId());
        return ResponseEntity.ok().build();
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

    @Operation(summary = "커뮤니티 게시글에 댓글 추가", description = "특정 커뮤니티 게시글에 댓글을 추가합니다.")
    @PostMapping("/{communityId}/comments")
    public ResponseEntity<CommunityCommentResponseDto> addComment(
            @PathVariable Long communityId,
            @RequestBody CommunityCommentRequestDto requestDto,
            @SessionAttribute(name = "userId", required = false) Long userId) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        CommunityCommentResponseDto responseDto = communityCommentService.addComment(communityId, requestDto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @Operation(summary = "커뮤니티 게시글 댓글 삭제", description = "특정 커뮤니티 게시글의 댓글을 삭제합니다.")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @SessionAttribute(name = "userId", required = false) Long userId) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        communityCommentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
