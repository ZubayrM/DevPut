package object.dto.response.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ErrorsAuthDto  {

    private String code;

    private String password;

    private String captcha;

    public ErrorsAuthDto(String code, String password, String captcha) {
        this.code = code;
        this.password = password;
        this.captcha = captcha;
    }
}
