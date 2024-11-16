package POT.DuoBloom.domain.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping("/{prefix}/presigned-url")
    public ResponseEntity<String> getPresignedUrl(@PathVariable String prefix, @RequestBody Map<String, String> requestBody) {
        String fileName = requestBody.get("imageName");
        String presignedUrl = fileService.getPreSignedUrl(prefix, fileName);
        return ResponseEntity.ok(presignedUrl);
    }
}
