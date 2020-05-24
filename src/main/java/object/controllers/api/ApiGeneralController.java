package object.controllers.api;
import lombok.AllArgsConstructor;
import object.dto.response.InitResponseDto;
import object.model.Posts;
import object.model.enums.ModerationStatus;
import object.repositories.PostsRepository;
import object.services.PostsService;
import object.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiGeneralController {

    private InitResponseDto initResponseDto;

    private PostsService postsService;

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
    public void moderation(@RequestParam("post_id") Integer postId,
                           @RequestParam String decision){
        postsService.moderationPost(postId, ModerationStatus.valueOf(decision.toUpperCase()), 1);
    }

    @GetMapping("/settings")
    public ResponseEntity getSettings(){
        return null;
    }

    @PutMapping("/settings")
    public ResponseEntity setSettings(){
        return null;
    }







}
