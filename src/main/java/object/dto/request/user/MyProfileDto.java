package object.dto.request.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyProfileDto {


    private String email;

    private String name;

    private String password;

    private MultipartFile photo;

    private Integer removePhoto;

}
