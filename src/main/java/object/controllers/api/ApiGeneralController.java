package object.controllers.api;
import object.dto.response.InitResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/")
public class ApiGeneralController {

    @GetMapping("init")
    public ResponseEntity getInfo(){
        return ResponseEntity.ok(new InitResponseDto());
    }

    @PostMapping("moderation")
    public void moderation(int postId, String decision){ }

    @GetMapping("settings")
    public ResponseEntity getSettings(){
        return null;
    }

    @PutMapping("settings")
    public ResponseEntity setSettings(){
        return null;
    }







}
