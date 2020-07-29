package object.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsDto {
    private Integer postsCount;
    private Integer likesCount;
    private Integer dislikesCount;
    private Integer viewsCount;
    private String firstPublication;
}
