package POT.DuoBloom.community.dto.request;

import POT.DuoBloom.community.entity.Type;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommunityRequestDto {
    private String content;
    private Type type;
    private List<String> tags;
}

