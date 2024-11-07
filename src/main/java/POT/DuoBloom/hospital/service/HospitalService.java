package POT.DuoBloom.hospital.service;

import POT.DuoBloom.hospital.dto.HospitalDto;
import POT.DuoBloom.hospital.dto.KeywordsMappingDto;
import POT.DuoBloom.hospital.entity.Hospital;
import POT.DuoBloom.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;

    public List<HospitalDto> findHospitalsByLocation(Long region, Long middle, Long detail) {
        List<Hospital> hospitals;
        if (detail != null) {
            hospitals = hospitalRepository.findByDetail(detail);
        } else if (middle != null) {
            hospitals = hospitalRepository.findByMiddle(middle);
        } else if (region != null) {
            hospitals = hospitalRepository.findByRegion(region);
        } else {
            hospitals = hospitalRepository.findAll();
        }
        return hospitals.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public HospitalDto convertToDto(Hospital hospital) {
        HospitalDto hospitalDto = new HospitalDto();
        hospitalDto.setHospitalId(hospital.getHospitalId());
        hospitalDto.setHospitalName(hospital.getHospitalName());
        hospitalDto.setRegion(hospital.getRegion());
        hospitalDto.setMiddle(hospital.getMiddle());
        hospitalDto.setDetail(hospital.getDetail());
        hospitalDto.setType(hospital.getType() != null ? hospital.getType().toString() : null);
        hospitalDto.setAddress(hospital.getAddress());
        hospitalDto.setPhone(hospital.getPhone());
        hospitalDto.setTime(hospital.getTime());
        hospitalDto.setHospitalInfo(hospital.getHospitalInfo());
        hospitalDto.setStaffInfo(hospital.getStaffInfo());
        hospitalDto.setLatitude(hospital.getLatitude());
        hospitalDto.setLongitude(hospital.getLongitude());
        hospitalDto.setLinkUrl(hospital.getLinkUrl());

        List<KeywordsMappingDto> keywordMappings = hospital.getKeywordMappings().stream()
                .map(mapping -> new KeywordsMappingDto(
                        mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().toString() : null))
                .collect(Collectors.toList());

        hospitalDto.setKeywordMappings(keywordMappings);
        return hospitalDto;
    }

}
