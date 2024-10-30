package POT.DuoBloom.point.pointTransaction;

import POT.DuoBloom.point.pointTransaction.dto.PointTransactionDTO;
import POT.DuoBloom.user.entity.User;
import POT.DuoBloom.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/points")
public class PointTransactionController {

    private final PointTransactionService pointTransactionService;

    @GetMapping
    public ResponseEntity<Integer> getCurrentPoints(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer points = pointTransactionService.getCurrentPoints(userId);
        return ResponseEntity.ok(points);
    }

    @GetMapping("/history")
    public ResponseEntity<List<PointTransactionDTO>> getPointHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PointTransactionDTO> history = pointTransactionService.getPointHistory(userId);
        return ResponseEntity.ok(history);
    }
}
