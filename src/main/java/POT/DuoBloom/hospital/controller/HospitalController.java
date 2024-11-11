package POT.DuoBloom.hospital.controller;

import POT.DuoBloom.hospital.service.HospitalService;
import POT.DuoBloom.hospital.dto.HospitalDto;
import POT.DuoBloom.hospital.dto.HospitalListDto;
import POT.DuoBloom.hospital.entity.Keyword;
import POT.DuoBloom.hospital.entity.HospitalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    @GetMapping("/search")
    public List<HospitalListDto> getHospitalsByFilters(
            @RequestParam(value = "region", required = false) Long region,
            @RequestParam(value = "middle", required = false) Long middle,
            @RequestParam(value = "detail", required = false) Long detail,
            @RequestParam(value = "keyword", required = false) Keyword keyword,
            @RequestParam(value = "type", required = false) HospitalType type) {
        return hospitalService.findHospitalsByFilters(region, keyword, type);
    }

    @GetMapping("/{hospitalId}")
    public HospitalDto getHospitalById(@PathVariable Integer hospitalId) {
        return hospitalService.getHospitalById(hospitalId);
    }
}
