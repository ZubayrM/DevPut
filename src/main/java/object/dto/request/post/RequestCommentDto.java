package object.dto.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCommentDto {

    @Nullable
    @JsonProperty("parent_id")
    private Integer parentId;

    @JsonProperty("post_id")
    private Integer postId;

    private String text;

}
