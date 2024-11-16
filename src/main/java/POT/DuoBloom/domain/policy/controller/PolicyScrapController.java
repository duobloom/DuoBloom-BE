package POT.DuoBloom.domain.policy.controller;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.policy.dto.request.ScrapRequestDto;
import POT.DuoBloom.domain.policy.dto.response.PolicyListDto;
import POT.DuoBloom.domain.policy.dto.response.ScrapResponseDto;
import POT.DuoBloom.domain.policy.service.PolicyScrapService;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policy-scrap")
public class PolicyScrapController {

    private final PolicyScrapService policyScrapService;
    private final UserService userService;

    // 정책 스크랩 추가
    @PostMapping
    public void scrapPolicy(HttpSession session, @RequestBody ScrapRequestDto requestDto) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        User user = userService.findById(userId);
        policyScrapService.scrapPolicy(user, requestDto.getPolicyId());
    }

    // 스크랩한 정책 조회
    @GetMapping
    public List<PolicyListDto> getPolicyScraps(HttpSession session) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        User user = userService.findById(userId);
        return policyScrapService.getPolicyScraps(user);
    }


    // 정책 스크랩 취소
    @DeleteMapping
    public void unsavePolicy(HttpSession session, @RequestBody ScrapRequestDto requestDto) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        User user = userService.findById(userId);
        policyScrapService.unsavePolicy(user, requestDto.getPolicyId());
    }
}
