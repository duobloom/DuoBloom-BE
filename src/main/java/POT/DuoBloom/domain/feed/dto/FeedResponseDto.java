package POT.DuoBloom.domain.feed.dto;

import POT.DuoBloom.domain.board.dto.response.BoardResponseDto;
import POT.DuoBloom.domain.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.domain.question.dto.response.QuestionWithAnswersDto;
import POT.DuoBloom.domain.user.dto.response.FeedUserProfileDto;
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
