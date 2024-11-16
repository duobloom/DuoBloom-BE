package POT.DuoBloom.domain.feed.service;

import POT.DuoBloom.domain.board.dto.response.BoardResponseDto;
import POT.DuoBloom.domain.board.service.BoardService;
import POT.DuoBloom.domain.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.domain.emotion.service.EmotionService;
import POT.DuoBloom.domain.feed.dto.FeedResponseDto;
import POT.DuoBloom.domain.question.dto.response.QuestionWithAnswersDto;
import POT.DuoBloom.domain.question.service.QuestionService;
import POT.DuoBloom.domain.user.dto.response.FeedUserProfileDto;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.repository.UserRepository;
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