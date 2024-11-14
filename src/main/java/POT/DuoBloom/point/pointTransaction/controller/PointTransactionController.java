package POT.DuoBloom.point.pointTransaction.controller;

import POT.DuoBloom.point.pointTransaction.service.PointTransactionService;
import POT.DuoBloom.point.pointTransaction.dto.PointTransactionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/points")
public class PointTransactionController {

    private final PointTransactionService pointTransactionService;

    @Operation(summary = "현재 포인트 조회", description = "로그인한 사용자의 현재 포인트를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "현재 포인트 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @GetMapping
    public ResponseEntity<Integer> getCurrentPoints(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer points = pointTransactionService.getCurrentPoints(userId);
        return ResponseEntity.ok(points);
    }

    @Operation(summary = "포인트 거래 내역 조회", description = "로그인한 사용자와 그의 커플의 포인트 거래 내역을 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "포인트 거래 내역 조회 성공"),
            @ApiResponse(responseCode = "401", description = "로그인이 필요합니다.")
    })
    @GetMapping("/history")
    public ResponseEntity<List<PointTransactionDTO>> getCombinedPointHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<PointTransactionDTO> history = pointTransactionService.getCombinedPointHistory(userId, userId);
        return ResponseEntity.ok(history);
    }

}
