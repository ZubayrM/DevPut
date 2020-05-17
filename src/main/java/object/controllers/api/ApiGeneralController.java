package object.controllers.api;
import object.dto.response.InitResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    @Autowired
    private InitResponseDto initResponseDto;

    @GetMapping("/init")
    public ResponseEntity getInfo(){
        return ResponseEntity.ok(initResponseDto);
    }

    @PostMapping("/moderation")
    public void moderation(int postId, String decision){ }

    @GetMapping("/settings")
    public ResponseEntity getSettings(){
        return null;
    }

    @PutMapping("/settings")
    public ResponseEntity setSettings(){
        return null;
    }







}
