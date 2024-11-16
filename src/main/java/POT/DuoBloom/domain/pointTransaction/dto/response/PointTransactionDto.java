package POT.DuoBloom.domain.pointTransaction.dto.response;

import POT.DuoBloom.domain.pointTransaction.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PointTransactionDto {
    private Long transactionId;
    private Integer amount;
    private TransactionType transactionType;
    private LocalDateTime createdAt;
    private Integer balance;
    private boolean mine;
}

