package object.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLDCVDto extends PostDto {

    private Integer liceCount;

    private Integer dislikeCount;

    private Integer commentCount;

    private Integer viewCount;

}
