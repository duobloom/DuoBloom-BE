package POT.DuoBloom.hospital.specification;

import POT.DuoBloom.hospital.entity.Hospital;
import POT.DuoBloom.hospital.entity.HospitalType;
import POT.DuoBloom.hospital.entity.Keyword;
import org.springframework.data.jpa.domain.Specification;

public class HospitalSpecifications {

    public static Specification<Hospital> hasRegion(Long region) {
        return (root, query, criteriaBuilder) ->
                region == null ? null : criteriaBuilder.equal(root.get("region"), region);
    }

    public static Specification<Hospital> hasMiddle(Long middle) {
        return (root, query, criteriaBuilder) ->
                middle == null ? null : criteriaBuilder.equal(root.get("middle"), middle);
    }

    public static Specification<Hospital> hasDetail(Long detail) {
        return (root, query, criteriaBuilder) ->
                detail == null ? null : criteriaBuilder.equal(root.get("detail"), detail);
    }

    public static Specification<Hospital> hasKeyword(Keyword keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null) return null;
            return criteriaBuilder.equal(root.join("keywordMappings").get("keyword").get("keyword"), keyword);
        };
    }

    public static Specification<Hospital> hasType(HospitalType type) {
        return (root, query, criteriaBuilder) ->
                type == null ? null : criteriaBuilder.equal(root.get("type"), type);
    }
}
