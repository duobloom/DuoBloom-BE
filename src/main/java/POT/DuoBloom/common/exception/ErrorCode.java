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
    HOSPITAL_NOT_FOUND(404, "해당 병원이 없습니다.");


    private final int status;
    private final String message;
}
