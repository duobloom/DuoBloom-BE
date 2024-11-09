package POT.DuoBloom.board.entity;

import POT.DuoBloom.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Entity
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer boardId;

    private String title;

    private String content;

    @Column(name = "feed_date", nullable = false)
    private LocalDate feedDate = LocalDate.now();

    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ElementCollection
    @CollectionTable(name = "board_photos", joinColumns = @JoinColumn(name = "board_id"))
    @Column(name = "photo_url")
    private List<String> photoUrls = new ArrayList<>();

    public void addPhotoUrl(String photoUrl) {
        this.photoUrls.add(photoUrl);
    }

    public Board(User user, String title, String content, LocalDateTime updatedAt) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.updatedAt = updatedAt != null ? updatedAt : LocalDateTime.now();
        this.feedDate = this.updatedAt.toLocalDate();
    }

    public void updateTitle(String title) {
        this.title = title;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
