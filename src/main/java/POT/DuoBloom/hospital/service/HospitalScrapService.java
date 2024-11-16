package POT.DuoBloom.hospital.service;

import POT.DuoBloom.common.exception.CustomException;
import POT.DuoBloom.common.exception.ErrorCode;
import POT.DuoBloom.hospital.dto.ScrapResponseDto;
import POT.DuoBloom.hospital.entity.Hospital;
import POT.DuoBloom.hospital.entity.HospitalScrap;
import POT.DuoBloom.hospital.repository.HospitalRepository;
import POT.DuoBloom.hospital.repository.HospitalScrapRepository;
import POT.DuoBloom.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HospitalScrapService {

    private final HospitalScrapRepository hospitalScrapRepository;
    private final HospitalRepository hospitalRepository;

    public void scrapHospital(User user, Integer hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_NOT_FOUND));
        HospitalScrap scrap = new HospitalScrap(user, hospital);
        hospitalScrapRepository.save(scrap);
    }

    public List<ScrapResponseDto> getHospitalScraps(User user) {
        List<HospitalScrap> scraps = hospitalScrapRepository.findByUser(user);
        if (scraps.isEmpty()) {
            throw new CustomException(ErrorCode.SCRAP_NOT_FOUND);
        }
        return scraps.stream()
                .map(scrap -> {
                    Hospital hospital = scrap.getHospital();
                    return new ScrapResponseDto(
                            hospital.getHospitalId(),
                            hospital.getHospitalName(),
                            hospital.getImageUrl()
                    );
                })
                .collect(Collectors.toList());
    }

    public void unsaveHospital(User user, Integer hospitalId) {
        Hospital hospital = hospitalRepository.findById(hospitalId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOSPITAL_NOT_FOUND));
        HospitalScrap scrap = hospitalScrapRepository.findByUserAndHospital(user, hospital)
                .orElseThrow(() -> new CustomException(ErrorCode.SCRAP_NOT_FOUND));
        hospitalScrapRepository.delete(scrap);
    }

}
