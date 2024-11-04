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
@Table(name = "emotions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"feed_date", "user_id"})
})
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionId;

    @Column(nullable = false)
    private Integer emoji;

    private String content;

    @Column(name = "feed_date", nullable = false)
    private LocalDate feedDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User users;

    public Emotion(User users, Integer emoji, String content, LocalDate feedDate) {
        this.users = users;
        this.emoji = emoji;
        this.content = content;
        this.feedDate = feedDate;
    }

    public void update(Integer emoji, String content) {
        this.emoji = emoji;
        this.content = content;
    }
}
