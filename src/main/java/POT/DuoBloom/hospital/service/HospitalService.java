package POT.DuoBloom.hospital.service;

import POT.DuoBloom.hospital.dto.HospitalDto;
import POT.DuoBloom.hospital.dto.HospitalListDto;
import POT.DuoBloom.hospital.dto.KeywordsMappingDto;
import POT.DuoBloom.hospital.entity.Hospital;
import POT.DuoBloom.hospital.entity.HospitalType;
import POT.DuoBloom.hospital.entity.Keyword;
import POT.DuoBloom.hospital.repository.HospitalRepository;
import POT.DuoBloom.region.entity.Detail;
import POT.DuoBloom.region.entity.Middle;
import POT.DuoBloom.region.entity.Region;
import POT.DuoBloom.region.repository.DetailRepository;
import POT.DuoBloom.region.repository.MiddleRepository;
import POT.DuoBloom.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalService {

    private final HospitalRepository hospitalRepository;
    private final RegionRepository regionRepository;
    private final MiddleRepository middleRepository;
    private final DetailRepository detailRepository;


    // 병원 이름으로 검색
    public List<HospitalListDto> findHospitalsByName(String name) {
        List<Hospital> hospitals = hospitalRepository.findByHospitalNameContaining(name);
        if (hospitals.isEmpty()) {
            return List.of();
        }
        return hospitals.stream().map(this::convertToListDto).collect(Collectors.toList());
    }


    // 병원 필터링
    public List<HospitalListDto> findHospitalsByFilters(Long region, Long middle, Long detail, Keyword keyword, HospitalType type) {
        List<Hospital> hospitals = hospitalRepository.findHospitalsByFilters(region, middle, detail, keyword, type);
        return hospitals.stream().map(this::convertToListDto).collect(Collectors.toList());
    }


    // 단일 병원 조회
    public HospitalDto getHospitalById(Integer hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new IllegalArgumentException("해당 병원이 없습니다: " + hospitalId));
        return convertToDto(hospital);
    }

    private String getRegionName(Long code) {
        Region region = regionRepository.findByRegionCode(code);
        return region != null ? region.getName() : null;
    }
    private String getMiddleName(Long code) {
        Middle middle = middleRepository.findByMiddleCode(code);
        return middle != null ? middle.getName() : null;
    }

    private String getDetailName(Long code) {
        Detail detail = detailRepository.findByDetailCode(code);
        return detail != null ? detail.getName() : null;
    }

    public HospitalListDto convertToListDto(Hospital hospital) {
        HospitalListDto hospitalListDto = new HospitalListDto();
        hospitalListDto.setHospitalId(hospital.getHospitalId());
        hospitalListDto.setHospitalName(hospital.getHospitalName());
        hospitalListDto.setImageUrl(hospital.getImageUrl());
        hospitalListDto.setRegion(getRegionName(hospital.getRegion()));
        hospitalListDto.setMiddle(getMiddleName(hospital.getMiddle()));
        hospitalListDto.setDetail(getDetailName(hospital.getDetail()));
        hospitalListDto.setType(hospital.getType() != null ? hospital.getType().toString() : null);
        hospitalListDto.setLatitude(hospital.getLatitude());
        hospitalListDto.setLongitude(hospital.getLongitude());
        hospitalListDto.setTime(hospital.getTime());

        List<KeywordsMappingDto> keywordMappings = hospital.getKeywordMappings().stream()
                .map(mapping -> new KeywordsMappingDto(
                        mapping.getKeyword() != null ? mapping.getKeyword().getKeyword().toString() : null))
                .collect(Collectors.toList());
        hospitalListDto.setKeywordMappings(keywordMappings);

        return hospitalListDto;
    }



    public HospitalDto convertToDto(Hospital hospital) {
        HospitalDto hospitalDto = new HospitalDto();
        hospitalDto.setHospitalId(hospital.getHospitalId());
        hospitalDto.setHospitalName(hospital.getHospitalName());
        hospitalDto.setImageUrl(hospital.getImageUrl());
        hospitalDto.setRegion(getRegionName(hospital.getRegion()));
        hospitalDto.setMiddle(getMiddleName(hospital.getMiddle()));
        hospitalDto.setDetail(getDetailName(hospital.getDetail()));
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
