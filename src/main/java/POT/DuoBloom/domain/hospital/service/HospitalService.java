package POT.DuoBloom.domain.hospital.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.hospital.dto.response.HospitalDto;
import POT.DuoBloom.domain.hospital.dto.response.HospitalListDto;
import POT.DuoBloom.domain.hospital.entity.Hospital;
import POT.DuoBloom.domain.hospital.entity.HospitalScrap;
import POT.DuoBloom.domain.hospital.entity.HospitalType;
import POT.DuoBloom.domain.hospital.entity.Keyword;
import POT.DuoBloom.domain.hospital.repository.HospitalRepository;
import POT.DuoBloom.domain.hospital.dto.response.KeywordsMappingDto;
import POT.DuoBloom.domain.hospital.repository.HospitalScrapRepository;
import POT.DuoBloom.domain.region.entity.Detail;
import POT.DuoBloom.domain.region.entity.Middle;
import POT.DuoBloom.domain.region.entity.Region;
import POT.DuoBloom.domain.region.repository.DetailRepository;
import POT.DuoBloom.domain.region.repository.MiddleRepository;
import POT.DuoBloom.domain.region.repository.RegionRepository;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
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
    private final HttpSession httpSession;
    private final UserRepository userRepository;
    private final HospitalScrapRepository hospitalScrapRepository;


    public List<HospitalListDto> findHospitalsByName(String name) {
        List<Hospital> hospitals = hospitalRepository.findByHospitalNameContaining(name);
        if (hospitals.isEmpty()) {
            throw new CustomException(ErrorCode.HOSPITAL_NOT_FOUND);
        }

        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }

        return hospitals.stream()
                .map(hospital -> {
                    HospitalListDto dto = convertToListDto(hospital);
                    boolean isScraped = hospitalScrapRepository.existsByHospital_HospitalIdAndUser_UserId(hospital.getHospitalId(), userId);
                    dto.setScraped(isScraped); // 로그 추가
                    System.out.println("Hospital ID: " + hospital.getHospitalId() + ", Is Scraped: " + isScraped);
                    return dto;
                })
                .collect(Collectors.toList());
    }



    // 병원 필터링
    public List<HospitalListDto> findHospitalsByFilters(Long region, Long middle, Long detail, Keyword keyword, HospitalType type) {
        List<Hospital> hospitals = hospitalRepository.findHospitalsByFiltersWithEntityGraph(region, middle, detail, keyword, type);

        if (hospitals.isEmpty()) {
            throw new CustomException(ErrorCode.HOSPITAL_NOT_FOUND);
        }

        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }

        return hospitals.stream()
                .map(hospital -> {
                    HospitalListDto dto = convertToListDto(hospital);
                    boolean isScraped = hospitalScrapRepository.existsByHospital_HospitalIdAndUser_UserId(hospital.getHospitalId(), userId);
                    dto.setScraped(isScraped);
                    return dto;
                })
                .collect(Collectors.toList());
    }



    // 단일 병원 조회
    public HospitalDto getHospitalById(Integer hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_NOT_FOUND));

        Long userId = (Long) httpSession.getAttribute("userId");
        if (userId == null) {
            throw new CustomException(ErrorCode.SESSION_USER_NOT_FOUND);
        }

        HospitalDto dto = convertToDto(hospital);
        boolean isScraped = hospitalScrapRepository.existsByHospital_HospitalIdAndUser_UserId(hospitalId, userId);
        dto.setScraped(isScraped);
        return dto;
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
