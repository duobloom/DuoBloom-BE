package POT.DuoBloom.domain.pointTransaction.dto.request;

import lombok.Data;

@Data
public class PointRequest {
    private Long userId;
    private Integer amount;
}