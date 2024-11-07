package POT.DuoBloom.hospital.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HospitalKeywords {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;

    @Column(nullable = false, unique = true)
    private String keyword;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL)
    private List<HospitalKeywordsMapping> hospitalMappings;
}
