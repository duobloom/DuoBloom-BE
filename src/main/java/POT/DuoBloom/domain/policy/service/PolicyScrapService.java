package POT.DuoBloom.domain.policy.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.policy.dto.response.KeywordsMappingDto;
import POT.DuoBloom.domain.policy.dto.response.PolicyListDto;
import POT.DuoBloom.domain.policy.entity.Policy;
import POT.DuoBloom.domain.policy.entity.PolicyScrap;
import POT.DuoBloom.domain.policy.repository.PolicyRepository;
import POT.DuoBloom.domain.policy.repository.PolicyScrapRepository;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyScrapService {

    private final PolicyScrapRepository scrapRepository;
    private final PolicyRepository policyRepository;
    private final UserRepository userRepository;

    /**
     * 정책 스크랩 등록
     */
    public void scrapPolicy(Integer policyId, Long userId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLICY_NOT_FOUND));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!scrapRepository.existsByPolicy_PolicyIdAndUser_UserId(policyId, userId)) {
            scrapRepository.save(new PolicyScrap(user, policy));
        }
    }

    /**
     * 정책 스크랩 해제
     */
    public void unscrapPolicy(Integer policyId, Long userId) {
        PolicyScrap scrap = scrapRepository.findByPolicy_PolicyIdAndUser_UserId(policyId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));

        scrapRepository.delete(scrap);
    }

    /**
     * 스크랩된 정책 목록 조회
     */
    public List<PolicyListDto> getScrappedPolicies(Long userId) {
        return scrapRepository.findByUser_UserId(userId).stream()
                .map(scrap -> {
                    Policy policy = scrap.getPolicy();
                    return new PolicyListDto(
                            policy.getPolicyId(),
                            policy.getPolicyName(),
                            policy.getPolicyHost(),
                            policy.getRegion() != null ? policy.getRegion().toString() : "", // null 처리
                            policy.getMiddle() != null ? policy.getMiddle().toString() : "", // null 처리
                            policy.getDetail() != null ? policy.getDetail().toString() : "", // null 처리
                            policy.getStartDate(),
                            policy.getEndDate(),
                            policy.getImageUrl(),
                            policy.getPolicyMappings().stream()
                                    .map(mapping -> new KeywordsMappingDto(
                                            mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().name() : null))
                                    .collect(Collectors.toList()),
                            true // 스크랩된 정책이므로 항상 true
                    );
                })
                .collect(Collectors.toList());
    }
}
