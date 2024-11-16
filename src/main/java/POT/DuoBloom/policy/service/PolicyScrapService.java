package POT.DuoBloom.policy.service;

import POT.DuoBloom.policy.dto.ScrapResponseDto;
import POT.DuoBloom.policy.entity.Policy;
import POT.DuoBloom.policy.entity.PolicyScrap;
import POT.DuoBloom.policy.repository.PolicyRepository;
import POT.DuoBloom.policy.repository.PolicyScrapRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyScrapService {

    private final PolicyScrapRepository policyScrapRepository;
    private final PolicyRepository policyRepository;

    public void scrapPolicy(User user, Integer policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 정책을 찾을 수 없습니다: " + policyId));
        PolicyScrap scrap = new PolicyScrap(user, policy);
        policyScrapRepository.save(scrap);
    }

    public List<ScrapResponseDto> getPolicyScraps(User user) {
        return policyScrapRepository.findByUser(user).stream()
                .map(scrap -> {
                    Policy policy = scrap.getPolicy();
                    return new ScrapResponseDto(
                            policy.getPolicyId(),
                            policy.getPolicyName(),
                            policy.getLinkUrl()
                    );
                })
                .collect(Collectors.toList());
    }

    public void unsavePolicy(User user, Integer policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("해당 정책을 찾을 수 없습니다: " + policyId));
        PolicyScrap scrap = policyScrapRepository.findByUserAndPolicy(user, policy)
                .orElseThrow(() -> new IllegalArgumentException("스크랩 내역이 없습니다."));
        policyScrapRepository.delete(scrap);
    }
}
