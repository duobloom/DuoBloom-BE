package POT.DuoBloom.board.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto {
    private Integer boardId;
    private String title;
    private String content;
    private LocalDateTime updatedAt;
    private List<BoardCommentDto> comments; // 단일 조회 시 사용
    private int likeCount;
    private int commentCount; // 전체 조회 시 댓글 개수 반환
}
