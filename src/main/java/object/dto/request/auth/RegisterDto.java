package object.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterDto extends LoginDto {

    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;

}
