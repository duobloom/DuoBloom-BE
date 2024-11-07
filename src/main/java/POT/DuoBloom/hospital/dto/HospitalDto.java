package POT.DuoBloom.hospital.dto;

import lombok.Data;
import java.util.List;

@Data
public class HospitalDto {
    private Integer hospitalId;
    private String hospitalName;
    private Long region;
    private Long middle;
    private Long detail;
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
