package POT.DuoBloom.domain.policy.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.policy.dto.response.KeywordsMappingDto;
import POT.DuoBloom.domain.policy.dto.response.PolicyListDto;
import POT.DuoBloom.domain.policy.entity.Policy;
import POT.DuoBloom.domain.policy.entity.PolicyScrap;
import POT.DuoBloom.domain.policy.repository.PolicyRepository;
import POT.DuoBloom.domain.policy.repository.PolicyScrapRepository;
import POT.DuoBloom.domain.region.service.RegionConversionService;
import POT.DuoBloom.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PolicyScrapService {

    private final PolicyScrapRepository policyScrapRepository;
    private final PolicyRepository policyRepository;
    private final RegionConversionService regionConversionService;

    public void scrapPolicy(User user, Integer policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLICY_NOT_FOUND));
        PolicyScrap scrap = new PolicyScrap(user, policy);
        policyScrapRepository.save(scrap);
    }

    public List<PolicyListDto> getPolicyScraps(User user) {
        return policyScrapRepository.findByUser(user).stream()
                .map(scrap -> convertToPolicyListDto(scrap.getPolicy()))
                .collect(Collectors.toList());
    }

    public void unsavePolicy(User user, Integer policyId) {
        Policy policy = policyRepository.findById(policyId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLICY_NOT_FOUND));
        PolicyScrap scrap = policyScrapRepository.findByUserAndPolicy(user, policy)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));
        policyScrapRepository.delete(scrap);
    }

    private PolicyListDto convertToPolicyListDto(Policy policy) {
        PolicyListDto policyListDto = new PolicyListDto();
        policyListDto.setPolicyId(policy.getPolicyId());
        policyListDto.setPolicyName(policy.getPolicyName());
        policyListDto.setPolicyHost(policy.getPolicyHost());
        policyListDto.setRegion(regionConversionService.convertRegionCodeToName(policy.getRegion()));
        policyListDto.setMiddle(regionConversionService.convertMiddleCodeToName(policy.getMiddle()));
        policyListDto.setDetail(regionConversionService.convertDetailCodeToName(policy.getDetail()));
        policyListDto.setStartDate(policy.getStartDate());
        policyListDto.setEndDate(policy.getEndDate());
        policyListDto.setImageUrl(policy.getImageUrl());

        // KeywordMappings 변환
        List<KeywordsMappingDto> keywordMappings = policy.getPolicyMappings().stream()
                .map(mapping -> new KeywordsMappingDto(
                        mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().toString() : null))
                .collect(Collectors.toList());
        policyListDto.setKeywordMappings(keywordMappings);

        return policyListDto;
    }
}
