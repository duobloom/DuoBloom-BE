package POT.DuoBloom.hospital.service;

import POT.DuoBloom.hospital.dto.HospitalDto;
import POT.DuoBloom.hospital.dto.KeywordsMappingDto;
import POT.DuoBloom.hospital.entity.Hospital;
import POT.DuoBloom.hospital.entity.HospitalType;
import POT.DuoBloom.hospital.entity.Keyword;
import POT.DuoBloom.hospital.repository.HospitalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;


    // 지역에 따라 병원 조회

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

    // 지역, 키워드, 타입에 따라 병원 조회
    public List<HospitalDto> findHospitalsByFilters(Long region, Keyword keyword, HospitalType type) {
        List<Hospital> hospitals;

        if (region != null && keyword != null && type != null) {
            hospitals = hospitalRepository.findByRegionAndKeywordMappings_Keyword_KeywordAndType(region, keyword, type);
        } else if (region != null && type != null) {
            hospitals = hospitalRepository.findByRegionAndType(region, type);
        } else if (keyword != null && type != null) {
            hospitals = hospitalRepository.findByKeywordMappings_Keyword_KeywordAndType(keyword, type);
        } else if (type != null) {
            hospitals = hospitalRepository.findByType(type);
        } else if (region != null && keyword != null) {
            hospitals = hospitalRepository.findByRegionAndKeywordMappings_Keyword_Keyword(region, keyword);
        } else if (keyword != null) {
            hospitals = hospitalRepository.findByKeywordMappings_Keyword_Keyword(keyword);
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
