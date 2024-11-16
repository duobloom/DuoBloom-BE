package POT.DuoBloom.domain.user.dto.request;

import POT.DuoBloom.domain.user.entity.Sex;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignupUserDto {
    private String nickname;
    private String email;
    private String password;
    private Sex sex;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate birth;

    private String authCode;
}
