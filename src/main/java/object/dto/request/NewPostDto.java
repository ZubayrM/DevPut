package object.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

//пока не использу
@Data
@NoArgsConstructor
public class NewPostDto {

    private String time;

    private Integer actual;

    private String title;

    private String text;

    private String tags;
}
