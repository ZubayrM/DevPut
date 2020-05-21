package object.dto.response.resultPost;

import lombok.Data;

@Data
public class ErrorPostDto implements ResultPostDto {
    private Boolean result = false;

    private ParamError errors;

}
