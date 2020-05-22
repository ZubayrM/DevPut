package object.controllers.api;
import object.config.security.UserAuthService;
import object.dto.response.InitResponseDto;
import object.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("/api")
public class ApiGeneralController {
    @Autowired
    private InitResponseDto initResponseDto;

    private UsersService userService;

    @GetMapping("/init")
    public ResponseEntity getInfo(){
        return ResponseEntity.ok(initResponseDto);
    }

    //todo ДОЛГИЙ ЯЩИК
    //временная версия - пока не могу проверить
    @PostMapping("/image")
    public ResponseEntity addImage(Image image){
        return  ResponseEntity.ok(userService.addImage(image));
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
