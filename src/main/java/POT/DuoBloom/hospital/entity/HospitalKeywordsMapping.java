package POT.DuoBloom.hospital.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class HospitalKeywordsMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "hospital_id", nullable = true)
    private Hospital hospital;

    @ManyToOne
    @JoinColumn(name = "keyword_id", nullable = false)
    private HospitalKeywords keyword;
}
