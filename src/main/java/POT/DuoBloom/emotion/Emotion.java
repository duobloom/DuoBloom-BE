package POT.DuoBloom.emotion;

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
        @UniqueConstraint(columnNames = {"date", "user_id"})
})
public class Emotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emotionId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Emoji emoji;

    private String content;

    @Column(nullable = false)
    private LocalDate date;

    public void update(Emoji emoji, String content) {
        this.emoji = emoji;
        this.content = content;
    }


    public void updateEmoji(Emoji emoji) {
        this.emoji = emoji;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateDate(LocalDate date) {
        this.date = date;
    }

    public void updateUsers(User users) {
        this.users = users;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;




}
