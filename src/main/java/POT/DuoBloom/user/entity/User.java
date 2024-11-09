package POT.DuoBloom.user.entity;

import POT.DuoBloom.board.entity.Board;
import POT.DuoBloom.board.entity.BoardLike;
import POT.DuoBloom.community.entity.Community;
import POT.DuoBloom.emotion.entity.Emotion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Entity
@Table(name = "users")
public class User implements Serializable {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    @Column(nullable = false)
    private LocalDate birth; // String 으로 수정 8자리로

    private Integer balance;

    @OneToOne
    @JoinColumn(name = "couple_user_id")
    private User coupleUser;

    public void updateUserId(Long userId) { this.userId = userId; } // 테스트용
    public void updateNickName(String nickname) { this.nickname = nickname; }
    public void updateEmail(String email) { this.email = email; }
    public void updatePassword(String password) { this.password = password; }
    public void updateBirth(LocalDate birth) {
        this.birth = birth;
    }
    public void updateBalance(int amount) {
        if (this.balance == null) {
            this.balance = 0;
        }
        this.balance += amount;
    }
    public void updateSex(Sex sex) { this.sex = sex; }

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Emotion> emotions = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardLike> boardLikes = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Community> communities = new ArrayList<>();


    public void setCoupleUser(User coupleUser) { this.coupleUser = coupleUser; }

    public Integer getCoupleBalance() { return coupleUser != null ? coupleUser.getBalance() : 0; }



}
