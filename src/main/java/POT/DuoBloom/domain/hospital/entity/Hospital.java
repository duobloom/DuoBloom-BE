package POT.DuoBloom.domain.hospital.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer hospitalId;

    @Column(name = "hospital_name", nullable = false)
    private String hospitalName;

    private Long region;
    private Long middle;
    private Long detail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private HospitalType type;

    private String address;
    private String phone;
    private String time;

    @Column(name = "hospital_info", columnDefinition = "TEXT")
    private String hospitalInfo;

    @Column(name = "staff_info", columnDefinition = "TEXT")
    private String staffInfo;

    private Double latitude;
    private Double longitude;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "image_url")
    private String imageUrl;

    @JsonManagedReference
    @OneToMany(mappedBy = "hospital", cascade = CascadeType.ALL)
    private List<HospitalKeywordsMapping> keywordMappings;

    @PrePersist
    public void prePersist() {
        if (this.type == null) {
            this.type = HospitalType.BASIC;
        }
        if (this.imageUrl == null) {
            this.imageUrl = "";
        }
    }
}
