package object.dto.response.resultPost;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OkPostDto implements ResultPostDto {

    private Boolean result = true;

}
