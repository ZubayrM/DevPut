package object.dto.response.resultPost;

import lombok.Data;
import object.dto.response.ResultDto;

@Data
public class ErrorPostDto extends ResultDto {

    private ParamError errors;

}
