package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.request.post.ModerationPostDto;
import object.services.GeneralService;
import object.services.ModService;
import object.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Log4j2
public class ApiModController {

    ModService modService;
    UsersService usersService;

    @PostMapping("/moderation")
    public ResponseEntity<Map<String,String>> moderation(@RequestBody ModerationPostDto request){
        modService.moderationPost(request.getPostId(),request.getDecision());
        Map<String, String> m = new HashMap<>();
        m.put("data", new Date().toString());
        return ResponseEntity.ok(m);
    }

    @GetMapping("/settings")
    public ResponseEntity<Map<String,String>> getSettings(){
        return ResponseEntity.ok(modService.getSetting());
    }

    @PutMapping("/settings")
    public ResponseEntity<HttpStatus> setSettings(@RequestBody Map<String, Boolean> globalSetting){
        if (usersService.getUser().getIsModerator() > 0) {
            log.info(globalSetting.keySet() + " " + globalSetting.values());
            modService.saveSettings(globalSetting);
        }
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

}
