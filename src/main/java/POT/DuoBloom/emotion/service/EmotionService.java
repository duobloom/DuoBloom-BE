package POT.DuoBloom.emotion.service;

import POT.DuoBloom.emotion.entity.Emotion;
import POT.DuoBloom.emotion.repository.EmotionRepository;
import POT.DuoBloom.emotion.dto.EmotionResponseDto;
import POT.DuoBloom.emotion.dto.EmotionUpdateDto;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final EmotionRepository emotionRepository;

    public List<EmotionResponseDto> findByFeedDateAndUsers(LocalDate feedDate, User users) {
        return emotionRepository.findByFeedDateAndUsers(feedDate, users)
                .stream()
                .map(emotion -> new EmotionResponseDto(
                        emotion.getEmotionId(),
                        emotion.getEmoji(),
                        emotion.getFeedDate()
                ))
                .collect(Collectors.toList());
    }


    @Transactional
    public boolean createEmotion(LocalDate feedDate, User users, EmotionUpdateDto emotionUpdateDto) {
        Optional<Emotion> existingEmotion = emotionRepository.findByFeedDateAndUsers(feedDate, users);

        if (existingEmotion.isPresent()) {
            return false;
        }

        Emotion newEmotion = new Emotion(users, emotionUpdateDto.getEmoji(), feedDate);
        emotionRepository.save(newEmotion);
        return true;
    }

    @Transactional
    public void updateEmotion(Long emotionId, EmotionUpdateDto emotionUpdateDto) {
        Emotion emotion = emotionRepository.findById(emotionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 감정을 찾을 수 없습니다. ID: " + emotionId));

        emotion.update(emotionUpdateDto.getEmoji());
    }
}
