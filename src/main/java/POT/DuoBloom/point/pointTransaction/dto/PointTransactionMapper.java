package POT.DuoBloom.point.pointTransaction.dto;

import POT.DuoBloom.point.pointTransaction.entity.PointTransaction;

public class PointTransactionMapper {

    public static PointTransactionDTO toDto(PointTransaction transaction, Long sessionUserId) {
        return PointTransactionDTO.builder()
                .transactionId(transaction.getTransactionId())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType())
                .createdAt(transaction.getCreatedAt())
                .balance(transaction.getBalance())
                .mine(transaction.getUser().getUserId().equals(sessionUserId))
                .build();
    }
}

