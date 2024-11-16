package POT.DuoBloom.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileEditDto {

    private String nickname;
    private LocalDate birth;
    private String profilePictureUrl;
    private String region;


}
