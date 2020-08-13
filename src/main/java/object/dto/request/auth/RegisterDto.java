package object.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterDto extends LoginDto {

    private String name;

    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;

    public RegisterDto(String eMail, String password, String name, String captcha, String captchaSecret) {
        super(eMail, password);
        this.name = name;
        this.captcha = captcha;
        this.captchaSecret = captchaSecret;
    }
}
