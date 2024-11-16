package POT.DuoBloom.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    POLICY_NOT_FOUND(404, "해당 정책을 찾을 수 없습니다."),
    USER_NOT_FOUND(404, "사용자를 찾을 수 없습니다."),
    SCRAP_NOT_FOUND(404, "스크랩 내역이 없습니다."),
    SESSION_USER_NOT_FOUND(401, "세션에 userId가 없습니다."),
    INVALID_INPUT_VALUE(400, "잘못된 입력 값입니다."),
    INTERNAL_SERVER_ERROR(500, "서버 오류가 발생했습니다."),
    HOSPITAL_NOT_FOUND(404, "해당 병원이 없습니다."),
    BOARD_NOT_FOUND(404, "게시글을 찾을 수 없습니다."),
    COMMENT_NOT_FOUND(404, "댓글을 찾을 수 없습니다."),
    UNAUTHORIZED_ACCESS(401, "권한이 없습니다."),
    ALREADY_LIKED(400, "이미 좋아요를 눌렀습니다."),
    NOT_LIKED(400, "좋아요를 누르지 않은 게시물입니다."),
    COUPLE_ONLY_ACCESS(403, "커플인 경우에만 접근할 수 있습니다."),
    INVALID_REQUEST(400, "잘못된 요청입니다."),
    FORBIDDEN_ACCESS(403, "접근 권한이 없습니다."),
    SESSION_EXPIRED(401, "세션이 만료되었습니다.");


    private final int status;
    private final String message;
}
