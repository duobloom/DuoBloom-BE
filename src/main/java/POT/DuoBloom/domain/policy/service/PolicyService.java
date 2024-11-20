package POT.DuoBloom.domain.policy.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.policy.dto.response.KeywordsMappingDto;
import POT.DuoBloom.domain.policy.dto.response.PolicyDto;
import POT.DuoBloom.domain.policy.dto.response.PolicyListDto;
import POT.DuoBloom.domain.policy.entity.Keyword;
import POT.DuoBloom.domain.policy.entity.Policy;
import POT.DuoBloom.domain.policy.repository.PolicyRepository;
import POT.DuoBloom.domain.policy.repository.PolicyScrapRepository;
import POT.DuoBloom.domain.region.service.RegionConversionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PolicyService {

    private final PolicyRepository policyRepository;
    private final RegionConversionService regionConversionService;
    private final PolicyScrapRepository scrapRepository;

    // 단일 정책 조회
    @Transactional(readOnly = true)
    public PolicyDto getPolicyById(Integer policyId, Long userId) {
        Policy singlePolicy = policyRepository.findById(policyId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLICY_NOT_FOUND));

        boolean isScraped = scrapRepository.existsByPolicy_PolicyIdAndUser_UserId(policyId, userId);

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
                PolicyDto.convertJsonToMap(singlePolicy.getTarget()),
                PolicyDto.convertJsonToMap(singlePolicy.getBenefit()),
                singlePolicy.getLinkUrl(),
                singlePolicy.getImageUrl(),
                singlePolicy.getPolicyMappings().stream()
                        .map(mapping -> new KeywordsMappingDto(
                                mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().name() : null))
                        .collect(Collectors.toList()),
                isScraped
        );
    }

    // 정책 목록 조회
    public List<PolicyListDto> getPolicies(Long region, Long middle, Long detail, Keyword keyword, Long userId) {
        List<Policy> policyList = policyRepository.findByRegionAndKeyword(region, middle, detail, keyword);

        if (policyList.isEmpty()) {
            throw new CustomException(ErrorCode.POLICY_NOT_FOUND);
        }

        return policyList.stream()
                .map(policy -> new PolicyListDto(
                        policy.getPolicyId(),
                        policy.getPolicyName(),
                        policy.getPolicyHost(),
                        regionConversionService.convertRegionCodeToName(policy.getRegion()),
                        regionConversionService.convertMiddleCodeToName(policy.getMiddle()),
                        regionConversionService.convertDetailCodeToName(policy.getDetail()),
                        policy.getStartDate(),
                        policy.getEndDate(),
                        policy.getImageUrl(),
                        policy.getPolicyMappings().stream()
                                .map(mapping -> new KeywordsMappingDto(
                                        mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().name() : null))
                                .collect(Collectors.toList()),
                        scrapRepository.existsByPolicy_PolicyIdAndUser_UserId(policy.getPolicyId(), userId)
                ))
                .collect(Collectors.toList());
    }
}
