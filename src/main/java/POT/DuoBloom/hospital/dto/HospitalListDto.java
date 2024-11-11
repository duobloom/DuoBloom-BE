package POT.DuoBloom.hospital.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalListDto {
    private Integer hospitalId;
    private String hospitalName;
    private String region;
    private String middle;
    private String detail;
    private String type;
    private List<KeywordsMappingDto> keywordMappings;
    private Double latitude;
    private Double longitude;
    private String time;
}
