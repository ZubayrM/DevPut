package object.dto.response.errors;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import object.dto.response.ResultDto;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorsMessageDto<E> extends ResultDto {
    private E errors;

    public ErrorsMessageDto(E error, boolean result) {
        setResult(result);
        errors = error;
    }
}
