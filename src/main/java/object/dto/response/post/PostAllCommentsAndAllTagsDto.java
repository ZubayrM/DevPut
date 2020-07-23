package object.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import object.dto.response.CommentDto;
import object.model.PostComments;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostAllCommentsAndAllTagsDto extends PostFullDto {

    private List<CommentDto> comments;

    private List<String> tags;

    private String text;

}
