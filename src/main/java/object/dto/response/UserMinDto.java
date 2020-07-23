package object.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class UserMinDto {

    private Integer id;

    private String name;

    public UserMinDto(Integer id, String name){
        this.id = id;
        this.name = name;
    }

}
