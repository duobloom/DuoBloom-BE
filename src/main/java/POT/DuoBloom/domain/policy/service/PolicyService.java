package POT.DuoBloom.domain.policy.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.policy.dto.response.KeywordsMappingDto;
import POT.DuoBloom.domain.policy.dto.response.PolicyDto;
import POT.DuoBloom.domain.policy.dto.response.PolicyListDto;
import POT.DuoBloom.domain.policy.entity.PolicyKeywordsMapping;
import POT.DuoBloom.domain.policy.repository.PolicyRepository;
import POT.DuoBloom.domain.policy.entity.Keyword;
import POT.DuoBloom.domain.policy.entity.Policy;
import POT.DuoBloom.domain.region.service.RegionConversionService;
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
    private final RegionConversionService regionConversionService;

    // 단일 정책 조회
    public PolicyDto getPolicyById(Integer policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLICY_NOT_FOUND));

        return new PolicyDto(
                policy.getPolicyId(),
                policy.getPolicyName(),
                policy.getPolicyHost(),
                regionConversionService.convertRegionCodeToName(policy.getRegion()),
                regionConversionService.convertMiddleCodeToName(policy.getMiddle()),
                regionConversionService.convertDetailCodeToName(policy.getDetail()),
                policy.getSex(),
                policy.getStartDate(),
                policy.getEndDate(),
                policy.getTarget(),
                policy.getLinkUrl(),
                policy.getPolicyMappings().stream()
                        .map(PolicyKeywordsMapping::getKeyword)
                        .map(k -> k.getKeyword().name())
                        .collect(Collectors.joining(", ")),
                policy.getPolicyMappings().stream()
                        .map(mapping -> new KeywordsMappingDto(
                                mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().name() : null))
                        .collect(Collectors.toList())
        );
    }

    // 정책 목록 조회
    public List<PolicyListDto> getPolicies(Long region, Long middle, Long detail, Keyword keyword) {
        List<Policy> policies = policyRepository.findByRegionAndKeyword(region, middle, detail, keyword);
        if (policies.isEmpty()) {
            throw new CustomException(ErrorCode.POLICY_NOT_FOUND);
        }
        return policies.stream()
                .map(policy -> new PolicyListDto(
                        policy.getPolicyId(),
                        policy.getPolicyName(),
                        policy.getPolicyHost(),
                        regionConversionService.convertRegionCodeToName(policy.getRegion()),
                        regionConversionService.convertMiddleCodeToName(policy.getMiddle()),
                        regionConversionService.convertDetailCodeToName(policy.getDetail()),
                        policy.getStartDate(),
                        policy.getEndDate(),
                        policy.getPolicyMappings().stream()
                                .map(mapping -> new KeywordsMappingDto(
                                        mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().name() : null))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
