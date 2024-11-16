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
        Policy singlePolicy = policyRepository.findById(policyId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLICY_NOT_FOUND));

        return new PolicyDto(
                singlePolicy.getPolicyId(),
                singlePolicy.getPolicyName(),
                singlePolicy.getPolicyHost(),
                regionConversionService.convertRegionCodeToName(singlePolicy.getRegion()),
                regionConversionService.convertMiddleCodeToName(singlePolicy.getMiddle()),
                regionConversionService.convertDetailCodeToName(singlePolicy.getDetail()),
                singlePolicy.getSex(),
                singlePolicy.getStartDate(),
                singlePolicy.getEndDate(),
                singlePolicy.getTarget(),
                singlePolicy.getLinkUrl(),
                singlePolicy.getImageUrl(),
                singlePolicy.getPolicyMappings().stream()
                        .map(mapping -> new KeywordsMappingDto(
                                mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().name() : null))
                        .collect(Collectors.toList())
        );
    }

    // 정책 목록 조회
    public List<PolicyListDto> getPolicies(Long region, Long middle, Long detail, Keyword keyword) {
        List<Policy> policyList = policyRepository.findByRegionAndKeyword(region, middle, detail, keyword);
        if (policyList.isEmpty()) {
            throw new CustomException(ErrorCode.POLICY_NOT_FOUND);
        }
        return policyList.stream()
                .map(policyItem -> new PolicyListDto(
                        policyItem.getPolicyId(),
                        policyItem.getPolicyName(),
                        policyItem.getPolicyHost(),
                        regionConversionService.convertRegionCodeToName(policyItem.getRegion()),
                        regionConversionService.convertMiddleCodeToName(policyItem.getMiddle()),
                        regionConversionService.convertDetailCodeToName(policyItem.getDetail()),
                        policyItem.getStartDate(),
                        policyItem.getEndDate(),
                        policyItem.getImageUrl(),
                        policyItem.getPolicyMappings().stream()
                                .map(mapping -> new KeywordsMappingDto(
                                        mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().name() : null))
                                .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }
}
