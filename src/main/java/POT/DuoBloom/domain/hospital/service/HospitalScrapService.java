package POT.DuoBloom.domain.hospital.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.domain.hospital.dto.response.HospitalListDto;
import POT.DuoBloom.domain.hospital.dto.response.KeywordsMappingDto;
import POT.DuoBloom.domain.hospital.entity.Hospital;
import POT.DuoBloom.domain.hospital.entity.HospitalScrap;
import POT.DuoBloom.domain.hospital.repository.HospitalRepository;
import POT.DuoBloom.domain.hospital.repository.HospitalScrapRepository;
import POT.DuoBloom.domain.region.service.RegionConversionService;
import POT.DuoBloom.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalScrapService {

    private final HospitalScrapRepository hospitalScrapRepository;
    private final HospitalRepository hospitalRepository;
    private final RegionConversionService regionConversionService;

    public void scrapHospital(User user, Integer hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_NOT_FOUND));

        if (hospitalScrapRepository.findByUserAndHospital(user, hospital).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_SCRAPPED);
        }

        HospitalScrap scrap = new HospitalScrap(user, hospital);
        hospitalScrapRepository.save(scrap);
    }

    public List<HospitalListDto> getHospitalScraps(User user) {
        List<HospitalScrap> scraps = hospitalScrapRepository.findByUser(user);
        if (scraps.isEmpty()) {
            throw new CustomException(ErrorCode.SCRAP_NOT_FOUND);
        }
        return scraps.stream()
                .map(scrap -> convertToListDto(scrap.getHospital()))
                .collect(Collectors.toList());
    }

    public void unsaveHospital(User user, Integer hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_NOT_FOUND));

        HospitalScrap scrap = hospitalScrapRepository.findByUserAndHospital(user, hospital)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));

        hospitalScrapRepository.delete(scrap);
    }

    private HospitalListDto convertToListDto(Hospital hospital) {
        HospitalListDto hospitalListDto = new HospitalListDto();
        hospitalListDto.setHospitalId(hospital.getHospitalId());
        hospitalListDto.setHospitalName(hospital.getHospitalName());
        hospitalListDto.setImageUrl(hospital.getImageUrl());
        hospitalListDto.setRegion(regionConversionService.convertRegionCodeToName(hospital.getRegion()));
        hospitalListDto.setMiddle(regionConversionService.convertMiddleCodeToName(hospital.getMiddle()));
        hospitalListDto.setDetail(regionConversionService.convertDetailCodeToName(hospital.getDetail()));
        hospitalListDto.setType(hospital.getType() != null ? hospital.getType().toString() : null);
        hospitalListDto.setLatitude(hospital.getLatitude());
        hospitalListDto.setLongitude(hospital.getLongitude());
        hospitalListDto.setTime(hospital.getTime());

        List<KeywordsMappingDto> keywordMappings = hospital.getKeywordMappings().stream()
                .map(mapping -> new KeywordsMappingDto(
                        mapping.getKeyword() != null ? String.valueOf(mapping.getKeyword().getKeyword()) : null))
                .collect(Collectors.toList());
        hospitalListDto.setKeywordMappings(keywordMappings);

        return hospitalListDto;
    }
}
