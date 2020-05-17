package object.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLDCVDto extends PostDto {

    private Integer likeCount;

    private Integer dislikeCount;

    private Integer commentCount;

    private Integer viewCount;

}
