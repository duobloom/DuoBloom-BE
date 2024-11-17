package POT.DuoBloom.domain.policy.controller;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.policy.dto.response.PolicyDto;
import POT.DuoBloom.domain.policy.dto.response.PolicyListDto;
import POT.DuoBloom.domain.policy.entity.Keyword;

import POT.DuoBloom.domain.policy.service.PolicyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/policies")
public class PolicyController {

    private final PolicyService policyService;

    // 정책 목록 필터링 조회
    @GetMapping("/filter")
    public List<PolicyListDto> filterPolicies(
            @RequestParam(required = false) Long region,
            @RequestParam(required = false) Long middle,
            @RequestParam(required = false) Long detail,
            @RequestParam(required = false) String keyword,
            @SessionAttribute(name = "userId", required = false) Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }

        Keyword keywordEnum = (keyword != null) ? Keyword.valueOf(keyword) : null;
        return policyService.getPolicies(region, middle, detail, keywordEnum, userId);
    }


    // 단일 정책 조회
    @GetMapping("/{policyId}")
    public PolicyDto getPolicyById(
            @PathVariable Integer policyId,
            @SessionAttribute(name = "userId", required = false) Long userId) {
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        return policyService.getPolicyById(policyId, userId);
    }
}
