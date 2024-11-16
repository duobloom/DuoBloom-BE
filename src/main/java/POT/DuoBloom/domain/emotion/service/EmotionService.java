package POT.DuoBloom.domain.emotion.service;

import POT.DuoBloom.domain.emotion.entity.Emotion;
import POT.DuoBloom.domain.emotion.repository.EmotionRepository;
import POT.DuoBloom.domain.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;

    public List<EmotionResponseDto> findByFeedDateAndUser(LocalDate feedDate, User user, Long loggedInUserId) {
        return emotionRepository.findByFeedDateAndUser(feedDate, user)
                .stream()
                .map(emotion -> new EmotionResponseDto(
                        emotion.getEmotionId(),
                        emotion.getEmoji(),
                        emotion.getFeedDate(),
                        emotion.getCreatedAt(),
                        user.getNickname(),
                        user.getProfilePictureUrl(),
                        user.getUserId().equals(loggedInUserId)
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Emotion createEmotion(LocalDate feedDate, User user, Integer emoji) {
        Emotion newEmotion = new Emotion(user, emoji, feedDate);
        return emotionRepository.save(newEmotion);
    }
}
