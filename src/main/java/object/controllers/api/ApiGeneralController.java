package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.request.post.ModerationPostDto;
import object.dto.response.InitResponseDto;
import object.model.Users;
import object.model.enums.ModerationStatus;
import object.services.GlobalSettingsService;
import object.services.PostsService;
import object.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Log4j2
public class ApiGeneralController {

    private InitResponseDto initResponseDto;

    private PostsService postsService;

    private UsersService userService;
    private GlobalSettingsService globalSettingsService;

    @GetMapping("/init")
    public ResponseEntity getInfo(){
        return ResponseEntity.ok(initResponseDto);
    }

    @PostMapping("/image")
    public ResponseEntity addImage(Image image){
        String imagePath = userService.addImage(image);
        return  ResponseEntity.ok(imagePath);
    }

    @PostMapping("/moderation")
    public ResponseEntity moderation(@RequestBody ModerationPostDto request){
        postsService.moderationPost(request.getPostId(),request.getDecision());
        Map<String, String> m = new HashMap<>();
        m.put("data", new Date().toString());
        return ResponseEntity.ok(m);
    }


    @GetMapping("/settings")
    public ResponseEntity getSettings(){
        log.info("settings");
        return ResponseEntity.ok(globalSettingsService.getSetting());
    }

    @PutMapping("/settings")
    public ResponseEntity setSettings(@RequestBody Map<String, Boolean> global_setting){
        log.info(global_setting.keySet() + " " + global_setting.values());
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }







}
