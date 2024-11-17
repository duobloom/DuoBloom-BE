package POT.DuoBloom.domain.hospital.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.hospital.dto.response.HospitalListDto;
import POT.DuoBloom.domain.hospital.entity.Hospital;
import POT.DuoBloom.domain.hospital.entity.HospitalScrap;
import POT.DuoBloom.domain.hospital.repository.HospitalRepository;
import POT.DuoBloom.domain.hospital.repository.HospitalScrapRepository;
import POT.DuoBloom.domain.region.entity.Detail;
import POT.DuoBloom.domain.region.entity.Middle;
import POT.DuoBloom.domain.region.entity.Region;
import POT.DuoBloom.domain.region.repository.DetailRepository;
import POT.DuoBloom.domain.region.repository.MiddleRepository;
import POT.DuoBloom.domain.region.repository.RegionRepository;
import POT.DuoBloom.domain.user.entity.User;
import POT.DuoBloom.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalScrapService {

    private final HospitalRepository hospitalRepository;
    private final HospitalScrapRepository hospitalScrapRepository;
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;
    private final MiddleRepository middleRepository;
    private final DetailRepository detailRepository;

    public void scrapHospital(Integer hospitalId, Long userId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_NOT_FOUND));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!hospitalScrapRepository.existsByHospital_HospitalIdAndUser_UserId(hospitalId, userId)) {
            hospitalScrapRepository.save(new HospitalScrap(hospital, user));
        }
    }

    public void unscrapHospital(Integer hospitalId, Long userId) {
        HospitalScrap scrap = hospitalScrapRepository.findByHospital_HospitalIdAndUser_UserId(hospitalId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));
        hospitalScrapRepository.delete(scrap);
    }

    public List<HospitalListDto> getScrappedHospitals(Long userId) {
        List<HospitalScrap> scraps = hospitalScrapRepository.findByUser_UserId(userId);
        return scraps.stream()
                .map(scrap -> convertToDto(scrap.getHospital()))
                .collect(Collectors.toList());
    }

    private HospitalListDto convertToDto(Hospital hospital) {
        HospitalListDto dto = new HospitalListDto();
        dto.setHospitalId(hospital.getHospitalId());
        dto.setHospitalName(hospital.getHospitalName());
        dto.setImageUrl(hospital.getImageUrl());
        dto.setRegion(getRegionName(hospital.getRegion())); // 지역 변환
        dto.setMiddle(getMiddleName(hospital.getMiddle())); // 중간 지역 변환
        dto.setDetail(getDetailName(hospital.getDetail())); // 상세 지역 변환
        dto.setType(hospital.getType() != null ? hospital.getType().toString() : null);
        dto.setLatitude(hospital.getLatitude());
        dto.setLongitude(hospital.getLongitude());
        dto.setTime(hospital.getTime());
        dto.setScraped(true); // 이미 스크랩된 병원이므로 true 설정
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
}
