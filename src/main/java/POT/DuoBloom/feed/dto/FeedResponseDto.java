package POT.DuoBloom.feed.dto;

import POT.DuoBloom.board.dto.BoardResponseDto;
import POT.DuoBloom.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.question.dto.QuestionWithAnswersDto;
import POT.DuoBloom.user.dto.FeedUserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedResponseDto {

    private LocalDate feedDate;
    private FeedUserProfileDto userProfile;
    private FeedUserProfileDto coupleProfile;
    private List<EmotionResponseDto> userEmotions;
    private List<EmotionResponseDto> coupleEmotions;
    private List<BoardResponseDto> userBoards;
    private List<BoardResponseDto> coupleBoards;
    private List<QuestionWithAnswersDto> questionsWithAnswers;

}
