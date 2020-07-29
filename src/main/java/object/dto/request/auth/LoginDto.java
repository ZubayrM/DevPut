package object.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDto {


    @JsonProperty("e_mail")
    private String eMail;

    private String password;
}
