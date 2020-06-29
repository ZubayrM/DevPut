package object.dto.request.post;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class NewPostDto {

    private String time;

    private Integer active;

    private String title;

    private String text;

    private String[] tags;
}
