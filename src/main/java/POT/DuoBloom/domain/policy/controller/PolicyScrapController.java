package POT.DuoBloom.domain.policy.controller;

import POT.DuoBloom.domain.policy.dto.response.PolicyListDto;
import POT.DuoBloom.domain.policy.service.PolicyScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/policy-scrap")
public class PolicyScrapController {

    private final PolicyScrapService scrapService;

    @PostMapping("/{policyId}")
    public ResponseEntity<Void> scrapPolicy(@PathVariable Integer policyId, @SessionAttribute Long userId) {
        scrapService.scrapPolicy(policyId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{policyId}")
    public ResponseEntity<Void> unscrapPolicy(@PathVariable Integer policyId, @SessionAttribute Long userId) {
        scrapService.unscrapPolicy(policyId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<PolicyListDto>> getScrappedPolicies(@SessionAttribute Long userId) {
        List<PolicyListDto> scrappedPolicies = scrapService.getScrappedPolicies(userId);
        return ResponseEntity.ok(scrappedPolicies);
    }
}
