package object.dto.response.auth;

import lombok.Data;
import lombok.NoArgsConstructor;
import object.dto.response.ResultDto;

@Data
@NoArgsConstructor
public class AuthUserResponseDto extends ResultDto {

    private UserAuthDto user;

    public AuthUserResponseDto(UserAuthDto user) {
        super(true);
        this.user = user;
    }

    public AuthUserResponseDto(boolean result){
        super(result);
    }
}
