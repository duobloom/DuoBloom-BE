package POT.DuoBloom.domain.community.controller;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.community.dto.request.CommunityCommentRequestDto;
import POT.DuoBloom.domain.community.dto.request.CommunityRequestDto;
import POT.DuoBloom.domain.community.dto.response.CommunityCommentResponseDto;
import POT.DuoBloom.domain.community.dto.response.CommunityFullResponseDto;
import POT.DuoBloom.domain.community.dto.response.CommunityListResponseDto;
import POT.DuoBloom.domain.community.dto.response.CommunityResponseDto;
import POT.DuoBloom.domain.community.service.CommunityCommentService;
import POT.DuoBloom.domain.community.service.CommunityService;
import POT.DuoBloom.domain.user.repository.UserRepository;
import POT.DuoBloom.domain.user.entity.User;
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
    public ResponseEntity<Void> createCommunity(
            @RequestBody CommunityRequestDto requestDto,
            @SessionAttribute(name = "userId", required = false) Long userId,
            HttpSession session) {

        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }

        User user = getUserFromSessionOrDb(userId, session);
        communityService.createCommunity(requestDto, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    @Operation(summary = "타입별 게시글 조회", description = "주어진 타입에 해당하는 모든 게시글을 조회합니다.")
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CommunityListResponseDto>> getCommunitiesByType(
            @PathVariable String type,
            @SessionAttribute(name = "userId", required = false) Long userId) {

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<CommunityListResponseDto> responseDtos = communityService.getCommunitiesByType(type, userId);
        return ResponseEntity.ok(responseDtos);
    }

    @Operation(summary = "커뮤니티 게시글 수정", description = "특정 커뮤니티 게시글을 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다."),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다.")
    })
    @PutMapping("/{communityId}")
    public ResponseEntity<Void> updateCommunity(
            @PathVariable Long communityId,
            @RequestBody CommunityRequestDto requestDto,
            @SessionAttribute(name = "userId", required = false) Long userId,
            HttpSession session) {

        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }

        User user = getUserFromSessionOrDb(userId, session);
        communityService.updateCommunity(communityId, requestDto, user.getUserId());
        return ResponseEntity.ok().build();
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
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
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
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }

        User user = getUserFromSessionOrDb(userId, session);
        communityService.toggleLike(communityId, user.getUserId());
        return ResponseEntity.ok().build();
    }

    private User getUserFromSessionOrDb(Long userId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
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
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
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
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }

        communityCommentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
