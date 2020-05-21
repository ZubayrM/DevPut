package object.dto.response.resultPostComment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorCommentDto implements ResultPostCommentDto {

    private Boolean result = false;

    ErrorText errors = new ErrorText();
}
