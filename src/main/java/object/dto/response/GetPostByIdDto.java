package object.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetPostByIdDto extends PostLDCVDto {

    private List<CommentDto> comments;

    private List<String> tags;

}
