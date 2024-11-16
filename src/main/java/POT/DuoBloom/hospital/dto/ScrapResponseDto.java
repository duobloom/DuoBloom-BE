package POT.DuoBloom.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScrapResponseDto {
    private Integer hospitalId;
    private String hospitalName;
    private String imageUrl;
}
