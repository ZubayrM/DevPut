package object.dto.request.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationPostDto {

    @JsonProperty("post_id")
    private Integer postId;

    private String decision;

}
