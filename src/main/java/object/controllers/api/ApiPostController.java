package object.controllers.api;
import object.dto.response.post.ListPostResponseDto;
import object.dto.response.post.PostAllCommentsAndAllTagsDto;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.services.PostCommentsService;
import object.services.PostVotesService;
import object.services.PostsService;
import object.services.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api")
public class ApiPostController {

    private PostsService postsService;
    private PostVotesService postVotesService;
    private PostCommentsService postCommentsService;
    private TagsService tagsService;

    @Autowired
    public ApiPostController(PostsService postsService, PostVotesService postVotesService, PostCommentsService postCommentsService, TagsService tagsService) {
        this.postsService = postsService;
        this.postVotesService = postVotesService;
        this.postCommentsService = postCommentsService;
        this.tagsService = tagsService;
    }

    @GetMapping("/post")
    public ResponseEntity<?> getAllPosts(@RequestParam Integer offset,
                                         @RequestParam Integer limit,
                                         @RequestParam String mode){
        ListPostResponseDto dto = postsService.getListPostResponseDtoByMode(offset, limit, Mode.valueOf(mode.toUpperCase()), 1);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/search")
    public ResponseEntity getPostsBySearch(@RequestParam Integer offset,
                                           @RequestParam Integer limit,
                                           @RequestParam String query){
        ListPostResponseDto dto = postsService.getListPostResponseDtoBySearch(offset, limit, query);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity getPost(@PathVariable Integer id){
        PostAllCommentsAndAllTagsDto dto = postsService.getPostAllCommentsAndAllTagsDto(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/byDate")
    public ResponseEntity getPostsByDate(@RequestParam Integer offset,
                                         @RequestParam Integer limit,
                                         @RequestParam String date){
        ListPostResponseDto dto = postsService.getListPostResponseDtoByDate(offset, limit, date);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/byTag")
    public ResponseEntity getPostsByTag(@RequestParam Integer offset,
                                        @RequestParam Integer limit,
                                        @RequestParam String tag){
        ListPostResponseDto dto = postsService.getListPostResponseDtoByTag(offset, limit, tag);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("post/moderation")
    public ResponseEntity getPostsModeration(@RequestParam Integer offset,
                                             @RequestParam Integer limit,
                                             @RequestParam String status){
        ListPostResponseDto dto = postsService.getPostDtoModeration(offset, limit, ModerationStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/my")
    public ResponseEntity getMyPosts(int id){
        return null;
    }

    @PostMapping("/post")
    public ResponseEntity addPost(LocalDate time, int active, String title, String text, String tags){
        return null;
    }

    @PostMapping("/image")
    public String addImage(String path){
        return null;
    }

    @PutMapping("/post/{id}")
    public ResponseEntity update(LocalDate time, int active, String title, String text, String tags, @PathVariable String id){
        return null;
    }

    @PostMapping("/comment")
    public ResponseEntity addComment(int parentId, int postId, String text){
        return null;
    }

    @GetMapping("/tag")
    public ResponseEntity getTags(String query){
        return null;
    }

    @PostMapping("/post/like")
    public ResponseEntity like(int postId){
        return null;
    }

    @PostMapping("/post/dislike")
    public ResponseEntity dislike(int postId){
        return null;
    }





}
