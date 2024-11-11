package POT.DuoBloom.feed.dto;

import POT.DuoBloom.board.dto.BoardResponseDto;
import POT.DuoBloom.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.question.dto.QuestionWithAnswersDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class FeedResponseDto {
    
    private LocalDate feedDate;
    private EmotionResponseDto userEmotion;
    private EmotionResponseDto coupleEmotion;
    private List<BoardResponseDto> userBoards;
    private List<BoardResponseDto> coupleBoards;
    private List<QuestionWithAnswersDto> questionsWithAnswers;
}
