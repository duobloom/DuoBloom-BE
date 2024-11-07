package POT.DuoBloom.hospital;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Keyword keyword;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL)
    private List<HospitalKeywordsMapping> hospitalMappings;
}
