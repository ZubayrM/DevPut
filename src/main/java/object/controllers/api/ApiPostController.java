package object.controllers.api;
import lombok.AllArgsConstructor;
import object.dto.response.ListPostResponseDto;
import object.dto.response.PostAllCommentsAndAllTagsDto;
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
@RequestMapping("api/")
@AllArgsConstructor
public class ApiPostController {

    private PostsService postsService;
    private PostVotesService postVotesService;
    private PostCommentsService postCommentsService;
    private TagsService tagsService;



    @GetMapping("post")
    public ResponseEntity findAllPosts(@RequestParam("offset") Integer offset,
                                    @RequestParam("limit") Integer limit,
                                    @RequestParam("mode") Mode mode){
        ListPostResponseDto dto =
                postCommentsService.getCountComment(
                        postVotesService.getCountVotes(
                                postsService.getListPost(offset, limit, mode, 1)));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("post/search")
    public ResponseEntity getPosts(@RequestParam Integer offset,
                                   @RequestParam Integer limit,
                                   @RequestParam String query){
        ListPostResponseDto dto =
                postCommentsService.getCountComment(
                        postVotesService.getCountVotes(
                                postsService.getListPost(offset, limit, query)));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("post/{id}")
    public ResponseEntity getPost(@PathVariable Integer id){

        PostAllCommentsAndAllTagsDto dto = tagsService.getAllTags(
                postCommentsService.getAllComment(
                        postVotesService.getCountVotes(
                                postsService.getPostById(id))));
        return ResponseEntity.ok(dto);
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
