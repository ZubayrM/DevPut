package object.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPhotoDto extends UserMinDto {
    private String photo;

    public UserPhotoDto (Integer id, String name, String photo){
        super(id, name);
        this.photo = photo;
    }
}
