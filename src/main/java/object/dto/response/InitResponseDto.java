package object.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InitResponseDto {

    @Value("${title}")
    private String title;

    @Value("${subtitle}")
    private String subtitle;

    @Value("${phone}")
    private String phone;

    @Value("${email}")
    private String email;

    @Value("${copyright}")
    private String copyright;

    @Value("${copyrightFrom}")
    private String copyrightFrom;
}
