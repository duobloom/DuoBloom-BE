package POT.DuoBloom.emotion.service;

import POT.DuoBloom.emotion.entity.Emotion;
import POT.DuoBloom.emotion.repository.EmotionRepository;
import POT.DuoBloom.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.user.entity.User;
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

    public List<EmotionResponseDto> findByFeedDateAndUser(LocalDate feedDate, User users) {
        return emotionRepository.findByFeedDateAndUser(feedDate, users)
                .stream()
                .map(emotion -> new EmotionResponseDto(
                        emotion.getEmotionId(),
                        emotion.getEmoji(),
                        emotion.getFeedDate()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public Emotion createOrUpdateEmotion(LocalDate feedDate, User user, Integer emoji) {
        // 해당 날짜에 기록된 감정이 있는지 확인
        List<Emotion> existingEmotions = emotionRepository.findByFeedDateAndUser(feedDate, user);

        if (!existingEmotions.isEmpty()) {
            // 첫 번째 감정의 이모지를 업데이트
            Emotion emotion = existingEmotions.get(0);
            emotion.updateEmoji(emoji);
            return emotion;
        }

        // 감정이 없으면 새로 생성
        Emotion newEmotion = new Emotion(user, emoji, feedDate);
        return emotionRepository.save(newEmotion);
    }
}
