package POT.DuoBloom.hospital.controller;

import POT.DuoBloom.hospital.service.HospitalService;
import POT.DuoBloom.hospital.dto.HospitalDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {

    private final HospitalService hospitalService;

    @Autowired
    public HospitalController(HospitalService hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/search")
    public List<HospitalDto> getHospitalsByLocation(
            @RequestParam(value = "region", required = false) Long region,
            @RequestParam(value = "middle", required = false) Long middle,
            @RequestParam(value = "detail", required = false) Long detail) {
        return hospitalService.findHospitalsByLocation(region, middle, detail);
    }
}
