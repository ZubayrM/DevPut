package object.dto.response.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import object.dto.response.ResultDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorsRegisterDto extends ResultDto {

    private String email;

    private String name;

    private String password;

    private String captcha;

    private String photo;

}
