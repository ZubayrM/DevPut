package object.controllers.api;
import object.dto.response.ListPostResponseDto;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.services.PostCommentsService;
import object.services.PostVotesService;
import object.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("api/")
public class ApiPostController {

    private PostsService postsService;
    private PostVotesService postVotesService;
    private PostCommentsService postCommentsService;

    @Autowired
    public ApiPostController(PostsService postsService, PostVotesService postVotesService, PostCommentsService postCommentsService) {
        this.postsService = postsService;
        this.postVotesService = postVotesService;
        this.postCommentsService = postCommentsService;
    }

    @GetMapping("post")
    public ResponseEntity postsList(@RequestParam(defaultValue = "0") Integer offset,
                                    @RequestParam(defaultValue = "10") Integer limit,
                                    @RequestBody Mode mode){
        ListPostResponseDto listPostResponseDto =
                postCommentsService.getCountComment(
                        postVotesService.getCountVotes(
                                postsService.getListPost(offset, limit, mode)));
        return ResponseEntity.ok(listPostResponseDto);
    }

    @GetMapping("post/search")
    public ResponseEntity getPosts(Integer offset, Integer limit, String query){
        return null;
    }

    @GetMapping("post/{id}")
    public ResponseEntity getPost(@PathVariable String id){
        return null;
    }

    @GetMapping("post/byDate")
    public ResponseEntity getPostsByDate(LocalDate date){
        return null;
    }

    @GetMapping("post/byTag")
    public ResponseEntity getPostsByTag(String tag){
        return null;
    }

    @GetMapping("post/moderation")
    public ResponseEntity getPostsModeration(int offset, int limit, ModerationStatus status){
        return null;
    }

    @GetMapping("post/my")
    public ResponseEntity getMyPosts(int id){
        return null;
    }

    @PostMapping("post")
    public ResponseEntity addPost(LocalDate time, int active, String title, String text, String tags){
        return null;
    }

    @PostMapping("image")
    public String addImage(String path){
        return null;
    }

    @PutMapping("post/{id}")
    public ResponseEntity update(LocalDate time, int active, String title, String text, String tags, @PathVariable String id){
        return null;
    }

    @PostMapping("comment")
    public ResponseEntity addComment(int parentId, int postId, String text){
        return null;
    }

    @GetMapping("tag")
    public ResponseEntity getTags(String query){
        return null;
    }

    @PostMapping("post/like")
    public ResponseEntity like(int postId){
        return null;
    }

    @PostMapping("post/dislike")
    public ResponseEntity dislike(int postId){
        return null;
    }





}
