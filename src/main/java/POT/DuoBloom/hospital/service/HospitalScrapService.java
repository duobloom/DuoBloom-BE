package POT.DuoBloom.hospital.service;

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
                .orElseThrow(() -> new IllegalArgumentException("해당 병원을 찾을 수 없습니다: " + hospitalId));
        HospitalScrap scrap = new HospitalScrap(user, hospital);
        hospitalScrapRepository.save(scrap);
    }

    public List<ScrapResponseDto> getHospitalScraps(User user) {
        return hospitalScrapRepository.findByUser(user).stream()
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
                .orElseThrow(() -> new IllegalArgumentException("해당 병원을 찾을 수 없습니다: " + hospitalId));
        HospitalScrap scrap = hospitalScrapRepository.findByUserAndHospital(user, hospital)
                .orElseThrow(() -> new IllegalArgumentException("스크랩 내역이 없습니다."));
        hospitalScrapRepository.delete(scrap);
    }
}
