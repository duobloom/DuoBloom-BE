package POT.DuoBloom.domain.hospital.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HospitalDto {
    private Integer hospitalId;
    private String hospitalName;
    private String imageUrl;
    private String region;
    private String middle;
    private String detail;
    private String type;
    private String address;
    private String phone;
    private String time;
    private String hospitalInfo;
    private String staffInfo;
    private Double latitude;
    private Double longitude;
    private String linkUrl;
    private List<KeywordsMappingDto> keywordMappings;
}
