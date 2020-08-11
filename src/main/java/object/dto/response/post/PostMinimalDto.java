package object.dto.response.post;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PostMinimalDto {
    private Integer id;

    private String timestamp;

    private String title;

    private String announce;
}
