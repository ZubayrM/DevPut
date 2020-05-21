package object.dto.response.resultPostComment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorText implements ResultPostCommentDto{
    private String text = "Текст комментария не задан или слишком короткий";
}
