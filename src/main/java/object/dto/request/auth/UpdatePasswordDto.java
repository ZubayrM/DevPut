package object.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordDto {
    private String captcha;

    @JsonProperty("captcha_secret")
    private String captchaSecret;

    private String code;

    private String password;
}
