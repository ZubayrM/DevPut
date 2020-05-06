package object.controllers.api;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/")
public class ApiPostController {

    @GetMapping("post")
    public ResponseEntity postsList(@DefaultValue("0") Integer offset,@DefaultValue("10") Integer limit, Mode mode){
        return ResponseEntity.ok(null);
}

    @GetMapping("post/search/")
    public ResponseEntity getPosts(Integer offset, Integer limit, String query){
        return null;
    }

    @GetMapping("post/{id}")
    public ResponseEntity getPost(int id){
        return null;
    }

    @GetMapping("post/byDate/")
    public ResponseEntity getPostsByDate(LocalDate date){
        return null;
    }

    @GetMapping("post/byTag")
    public ResponseEntity getPostsByTag(String tag){
        return null;
    }

    @GetMapping("post/moderation/")
    public ResponseEntity getPostsModeration(int offset, int limit, ModerationStatus status){
        return null;
    }

    @GetMapping("post/my")
    public ResponseEntity getMyPosts(int id){
        return null;
    }

    @PostMapping("post/")
    public ResponseEntity addPost(LocalDate time, int active, String title, String text, String tags){
        return null;
    }

    @PostMapping("image/")
    public String addImage(String path){
        return null;
    }

    @PutMapping("post/{id}")
    public ResponseEntity update(LocalDate time, int active, String title, String text, String tags){
        return null;
    }

    @PostMapping("comment/")
    public ResponseEntity addComment(int parentId, int postId, String text){
        return null;
    }

    @GetMapping("tag/")
    public ResponseEntity getTags(String query){
        return null;
    }

    @PostMapping("post/like/")
    public ResponseEntity like(int postId){
        return null;
    }

    @PostMapping("post/dislike/")
    public ResponseEntity dislike(int postId){
        return null;
    }





}
