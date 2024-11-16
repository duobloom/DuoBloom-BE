package POT.DuoBloom.feed.service;

import POT.DuoBloom.board.dto.BoardResponseDto;
import POT.DuoBloom.board.service.BoardService;
import POT.DuoBloom.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.emotion.service.EmotionService;
import POT.DuoBloom.feed.dto.FeedResponseDto;
import POT.DuoBloom.question.dto.QuestionWithAnswersDto;
import POT.DuoBloom.question.service.QuestionService;
import POT.DuoBloom.user.dto.FeedUserProfileDto;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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

        // 사용자 프로필 생성
        FeedUserProfileDto userProfile = new FeedUserProfileDto(
                user.getNickname(),
                user.getProfilePictureUrl(),
                user.getBirth(),
                user.getBalance()
        );

        // 커플 사용자 프로필 생성
        FeedUserProfileDto coupleProfile = new FeedUserProfileDto(
                coupleUser.getNickname(),
                coupleUser.getProfilePictureUrl(),
                coupleUser.getBirth(),
                coupleUser.getBalance()
        );

        // Emotion 조회 및 작성자 정보 설정
        List<EmotionResponseDto> userEmotions = emotionService.findByFeedDateAndUser(feedDate, user, userId);
        List<EmotionResponseDto> coupleEmotions = emotionService.findByFeedDateAndUser(feedDate, coupleUser, userId);

        // Board 조회
        List<BoardResponseDto> userBoards = boardService.getBoardsByDateAndUser(feedDate, user, userId);
        List<BoardResponseDto> coupleBoards = boardService.getBoardsByDateAndUser(feedDate, coupleUser, userId);

        // Question + Answer 조회
        List<QuestionWithAnswersDto> questionsWithAnswers = questionService.getQuestionsWithAnswerStatus(feedDate, userId);

        return new FeedResponseDto(
                feedDate,
                userProfile,
                coupleProfile,
                userEmotions,
                coupleEmotions,
                userBoards,
                coupleBoards,
                questionsWithAnswers
        );
    }
}