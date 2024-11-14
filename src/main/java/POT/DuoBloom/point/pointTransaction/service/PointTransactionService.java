package POT.DuoBloom.point.pointTransaction.service;

import POT.DuoBloom.point.pointTransaction.dto.PointTransactionDTO;
import POT.DuoBloom.point.pointTransaction.dto.PointTransactionMapper;
import POT.DuoBloom.point.pointTransaction.entity.PointTransaction;
import POT.DuoBloom.point.pointTransaction.entity.TransactionType;
import POT.DuoBloom.point.pointTransaction.repository.PointTransactionRepository;
import POT.DuoBloom.user.repository.UserRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointTransactionService {

    private final PointTransactionRepository pointTransactionRepository;
    private final UserRepository userRepository;

    public Integer getCurrentPoints(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        return user.getBalance();
    }

    public List<PointTransactionDTO> getCombinedPointHistory(Long userId, Long sessionUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<PointTransaction> transactions = pointTransactionRepository.findByUser_UserId(userId);
        if (user.getCoupleUser() != null) {
            transactions.addAll(pointTransactionRepository.findByUser_UserId(user.getCoupleUser().getUserId()));
        }

        return transactions.stream()
                .map(transaction -> PointTransactionMapper.toDto(transaction, sessionUserId))
                .sorted(Comparator.comparing(PointTransactionDTO::getCreatedAt).reversed())
                .collect(Collectors.toList());
    }

    @Transactional
    public PointTransactionDTO increasePoints(User user, Integer amount, TransactionType transactionType) {
        log.info("Increasing points for user {} by {} points", user.getUserId(), amount);
        PointTransactionDTO result = processTransaction(user, amount, transactionType);
        log.info("Points increased for user {}: new balance is {}", user.getUserId(), user.getBalance());
        return result;
    }

    @Transactional
    public PointTransactionDTO decreasePoints(User user, Integer amount, TransactionType transactionType) {
        if (user.getBalance() < amount) {
            log.warn("Attempt to deduct {} points from user {} failed due to insufficient balance", amount, user.getUserId());
            throw new IllegalArgumentException("Insufficient points.");
        }
        log.info("Decreasing points for user {} by {} points", user.getUserId(), amount);
        PointTransactionDTO result = processTransaction(user, -amount, transactionType);
        log.info("Points decreased for user {}: new balance is {}", user.getUserId(), user.getBalance());
        return result;
    }

    private PointTransactionDTO processTransaction(User user, Integer amount, TransactionType transactionType) {
        user.updateBalance(amount);

        PointTransaction transaction = PointTransaction.builder()
                .amount(amount)
                .transactionType(transactionType)
                .createdAt(LocalDateTime.now())
                .balance(user.getBalance())
                .user(user)
                .build();

        userRepository.save(user);
        pointTransactionRepository.save(transaction);

        log.info("Processed transaction for user {}: amount={}, transactionType={}, new balance={}",
                user.getUserId(), amount, transactionType, user.getBalance());

        return PointTransactionMapper.toDto(transaction, user.getUserId());
    }
}
