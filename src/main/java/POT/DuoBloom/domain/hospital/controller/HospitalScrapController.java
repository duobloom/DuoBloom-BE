package POT.DuoBloom.domain.hospital.controller;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.hospital.dto.response.HospitalListDto;
import POT.DuoBloom.domain.hospital.service.HospitalScrapService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hospital-scrap")
public class HospitalScrapController {

    private final HospitalScrapService hospitalScrapService;
    private final HttpSession httpSession;

    @PostMapping("/{hospitalId}")
    public ResponseEntity<String> scrapHospital(@PathVariable Integer hospitalId) {
        Long userId = getUserIdFromSession();
        hospitalScrapService.scrapHospital(hospitalId, userId);
        return ResponseEntity.ok("Hospital Scrapped");
    }

    @DeleteMapping("/{hospitalId}")
    public ResponseEntity<String> unscriptHospital(@PathVariable Integer hospitalId) {
        Long userId = getUserIdFromSession();
        hospitalScrapService.unscrapHospital(hospitalId, userId);
        return ResponseEntity.ok("Hospital Unscrapped");
    }

    @GetMapping
    public ResponseEntity<List<HospitalListDto>> getScrappedHospitals() {
        Long userId = getUserIdFromSession();
        List<HospitalListDto> scrappedHospitals = hospitalScrapService.getScrappedHospitals(userId);
        return ResponseEntity.ok(scrappedHospitals);
    }

    private Long getUserIdFromSession() {
        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }
        return userId;
    }
}
