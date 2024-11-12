package POT.DuoBloom.feed.service;

import POT.DuoBloom.board.dto.BoardResponseDto;
import POT.DuoBloom.board.service.BoardService;
import POT.DuoBloom.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.emotion.service.EmotionService;
import POT.DuoBloom.feed.dto.FeedResponseDto;
import POT.DuoBloom.question.dto.QuestionWithAnswersDto;
import POT.DuoBloom.question.service.QuestionService;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeedService {

    private final EmotionService emotionService;
    private final BoardService boardService;
    private final QuestionService questionService;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public FeedResponseDto getDailyFeed(LocalDate feedDate, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // 커플 사용자 가져오기
        User coupleUser = user.getCoupleUser();
        if (coupleUser == null) {
            throw new IllegalStateException("커플 연결이 필요합니다.");
        }

        // Emotion 조회
        List<EmotionResponseDto> userEmotion = emotionService.findByFeedDateAndUser(feedDate, user);
        List<EmotionResponseDto> coupleEmotion = emotionService.findByFeedDateAndUser(feedDate, coupleUser);

        // Board 조회
        List<BoardResponseDto> userBoards = boardService.getBoardsByDateAndUser(feedDate, user, userId);
        List<BoardResponseDto> coupleBoards = boardService.getBoardsByDateAndUser(feedDate, coupleUser, userId);

        // Question + Answer 조회 후 변환
        List<QuestionWithAnswersDto> questionsWithAnswers = questionService.getQuestionsWithAnswerStatus(feedDate, userId)
                .stream()
                .map(questionDto -> new QuestionWithAnswersDto(
                        questionDto.getQuestionId(),
                        questionDto.getContent(),
                        questionDto.isMyAnswerStatus(),
                        questionDto.isCoupleAnswerStatus(),
                        questionDto.getAnswers()
                ))
                .collect(Collectors.toList());

        return new FeedResponseDto(
                feedDate,
                userEmotion.isEmpty() ? null : userEmotion.get(0),
                coupleEmotion.isEmpty() ? null : coupleEmotion.get(0),
                userBoards,
                coupleBoards,
                questionsWithAnswers
        );
    }
}
