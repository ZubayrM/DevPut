package object.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import object.dto.response.UserMinDto;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostAndAuthorDto  extends  PostMinimalDto{


    private UserMinDto user;


}
