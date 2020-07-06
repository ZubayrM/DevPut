package object.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import object.dto.response.UserResponseDto;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAndAuthorDto  extends  PostMinimalDto{


    private UserResponseDto user;


}
