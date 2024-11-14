package POT.DuoBloom.point.pointTransaction.dto;

import POT.DuoBloom.point.pointTransaction.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointTransactionDTO {
    private Long transactionId;
    private Integer amount;
    private TransactionType transactionType;
    private LocalDateTime createdAt;
    private Integer balance;
    private boolean mine;
}

