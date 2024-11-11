package POT.DuoBloom.region.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Middle {

    @Id
    private Long middleCode;

    private String name;

    private Long regionCode; // 상위 region 코드 참조
}

