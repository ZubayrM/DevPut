package object.dto.request.post;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;


@Data
@NoArgsConstructor
public class NewPostDto {

    private Long timestamp;

    private Integer active;

    private String title;

    private String text;

    private String[] tags;
}
