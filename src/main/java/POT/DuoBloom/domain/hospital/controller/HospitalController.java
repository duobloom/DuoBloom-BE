package POT.DuoBloom.domain.hospital.controller;

import POT.DuoBloom.domain.hospital.dto.response.HospitalDto;
import POT.DuoBloom.domain.hospital.dto.response.HospitalListDto;
import POT.DuoBloom.domain.hospital.entity.HospitalType;
import POT.DuoBloom.domain.hospital.entity.Keyword;
import POT.DuoBloom.domain.hospital.service.HospitalService;
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

    @GetMapping("/filter")
    public List<HospitalListDto> getHospitalsByFilters(
            @RequestParam(value = "region", required = false) Long region,
            @RequestParam(value = "middle", required = false) Long middle,
            @RequestParam(value = "detail", required = false) Long detail,
            @RequestParam(value = "keyword", required = false) Keyword keyword,
            @RequestParam(value = "type", required = false) HospitalType type) {
        return hospitalService.findHospitalsByFilters(region, middle, detail, keyword, type);
    }

    @GetMapping("/search")
    public List<HospitalListDto> searchHospitalsByName(@RequestParam("name") String name) {
        return hospitalService.findHospitalsByName(name);
    }

    @GetMapping("/{hospitalId}")
    public HospitalDto getHospitalById(@PathVariable Integer hospitalId) {
        return hospitalService.getHospitalById(hospitalId);
    }


}
