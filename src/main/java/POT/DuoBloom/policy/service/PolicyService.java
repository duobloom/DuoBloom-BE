package POT.DuoBloom.policy.service;

import POT.DuoBloom.policy.dto.PolicyDto;
import POT.DuoBloom.policy.entity.Keyword;
import POT.DuoBloom.policy.entity.Policy;
import POT.DuoBloom.policy.repository.PolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import POT.DuoBloom.policy.entity.PolicyKeywordsMapping;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;

    public List<PolicyDto> getPolicies(Long region, Long middle, Long detail, Keyword keyword) {
        List<Policy> policies = policyRepository.findByRegionAndKeyword(region, middle, detail, keyword);
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
