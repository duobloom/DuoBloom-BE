package POT.DuoBloom.domain.policy.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.policy.dto.response.PolicyDto;
import POT.DuoBloom.domain.policy.entity.PolicyKeywordsMapping;
import POT.DuoBloom.domain.policy.repository.PolicyRepository;
import POT.DuoBloom.domain.policy.entity.Keyword;
import POT.DuoBloom.domain.policy.entity.Policy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    public List<PolicyDto> getPolicies(Long region, Long middle, Long detail, Keyword keyword) {
        List<Policy> policies = policyRepository.findByRegionAndKeyword(region, middle, detail, keyword);
        if (policies.isEmpty()) {
            throw new CustomException(ErrorCode.POLICY_NOT_FOUND);
        }
        return policies.stream()
                .map(policy -> new PolicyDto(
                        policy.getPolicyId(),
                        policy.getPolicyName(),
                        policy.getPolicyHost(),
                        policy.getRegion(),
                        policy.getMiddle(),
                        policy.getDetail(),
                        policy.getSex(),
                        policy.getTarget(),
                        policy.getLinkUrl(),
                        policy.getPolicyMappings().stream()
                                .map(PolicyKeywordsMapping::getKeyword)
                                .map(k -> k.getKeyword().name())
                                .collect(Collectors.joining(", "))
                ))
                .collect(Collectors.toList());
    }

}
