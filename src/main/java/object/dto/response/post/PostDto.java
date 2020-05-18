package object.dto.response.post;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import object.dto.response.UserResponseDto;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Integer id;

    private String time;

    private UserResponseDto user;

    private String title;

    private String announce;
}
