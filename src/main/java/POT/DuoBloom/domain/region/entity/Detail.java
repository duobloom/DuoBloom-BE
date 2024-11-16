package POT.DuoBloom.domain.region.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Detail {

    @Id
    private Long detailCode;

    private String name;

    private Long middleCode;
}
