package POT.DuoBloom.domain.policy.controller;

import POT.DuoBloom.domain.policy.dto.response.PolicyDto;
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


    @GetMapping("/filter")
    public List<PolicyDto> filterPolicies(
            @RequestParam(required = false) Long region,
            @RequestParam(required = false) Long middle,
            @RequestParam(required = false) Long detail,
            @RequestParam(required = false) String keyword) {
        Keyword keywordEnum = (keyword != null) ? Keyword.valueOf(keyword) : null;
        return policyService.getPolicies(region, middle, detail, keywordEnum);
    }

}
