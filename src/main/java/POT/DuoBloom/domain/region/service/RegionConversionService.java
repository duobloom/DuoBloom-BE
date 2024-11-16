package POT.DuoBloom.domain.region.service;

import POT.DuoBloom.domain.region.repository.DetailRepository;
import POT.DuoBloom.domain.region.repository.MiddleRepository;
import POT.DuoBloom.domain.region.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegionConversionService {

    private final RegionRepository regionRepository;
    private final MiddleRepository middleRepository;
    private final DetailRepository detailRepository;

    public String convertRegionCodeToName(Long regionCode) {
        if (regionCode == null) {
            return null;
        }
        return regionRepository.findNameByCode(regionCode).orElse("");
    }

    public String convertMiddleCodeToName(Long middleCode) {
        if (middleCode == null) {
            return null;
        }
        return middleRepository.findNameByCode(middleCode).orElse("");
    }

    public String convertDetailCodeToName(Long detailCode) {
        if (detailCode == null) {
            return null;
        }
        return detailRepository.findNameByCode(detailCode).orElse("");
    }
}
