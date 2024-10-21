package POT.DuoBloom.user.entity;

import POT.DuoBloom.emotion.Emotion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Entity
@Table(name = "users")
public class User {

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
    private LocalDate birth;

    private Integer balance;

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

}
