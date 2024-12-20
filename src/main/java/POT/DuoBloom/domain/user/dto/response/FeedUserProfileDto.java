package POT.DuoBloom.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeedUserProfileDto {
    private String nickname;
    private String profilePictureUrl;
    private LocalDate birth;
    private Integer balance;
}
