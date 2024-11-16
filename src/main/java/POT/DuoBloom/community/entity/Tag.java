package POT.DuoBloom.community.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tagId;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Community> communities = new HashSet<>();

    public Tag(String name) {
        this.name = name;
    }

}
