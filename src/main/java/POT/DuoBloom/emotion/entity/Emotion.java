package POT.DuoBloom.emotion.entity;

import POT.DuoBloom.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Entity
@Table(name = "emotions")
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionId;

    @Column(nullable = false)
    private Integer emoji;

    @Column(name = "feed_date", nullable = false)
    private LocalDate feedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Emotion(User user, Integer emoji, LocalDate feedDate) {
        this.user = user;
        this.emoji = emoji;
        this.feedDate = feedDate;
    }

    public void updateEmoji(Integer emoji) {
        this.emoji = emoji;
    }
}
