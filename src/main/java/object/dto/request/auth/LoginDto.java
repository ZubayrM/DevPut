package object.dto.request.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.JoinColumn;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {


    @JsonProperty("e_mail")
    private String eMail;

    private String password;
}
