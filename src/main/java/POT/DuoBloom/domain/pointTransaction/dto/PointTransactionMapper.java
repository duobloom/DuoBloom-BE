package POT.DuoBloom.domain.pointTransaction.dto;

import POT.DuoBloom.domain.pointTransaction.dto.response.PointTransactionDto;
import POT.DuoBloom.domain.pointTransaction.entity.PointTransaction;

public class PointTransactionMapper {

    public static PointTransactionDto toDto(PointTransaction transaction, Long sessionUserId) {
        return PointTransactionDto.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .createdAt(transaction.getCreatedAt())
                .balance(transaction.getBalance())
                .mine(transaction.getUser().getUserId().equals(sessionUserId))
                .build();
    }
}

