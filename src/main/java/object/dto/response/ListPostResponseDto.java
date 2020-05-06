package object.dto.response;

import lombok.*;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ListPostResponseDto {

    private Integer count;

    @Singular
    private List<PostLDCVDto> posts;

}
