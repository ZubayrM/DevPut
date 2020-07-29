package object.controllers.api;

import lombok.AllArgsConstructor;
import object.dto.request.post.CommentDto;
import object.dto.request.post.NewPostDto;
import object.dto.request.post.VotesDto;
import object.dto.response.ResultDto;
import object.dto.response.post.ListPostResponseDto;
import object.dto.response.post.PostAllCommentsAndAllTagsDto;
import object.dto.response.resultPostComment.ResultPostCommentDto;
import object.dto.response.tag.TagsDto;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.services.PostVotesService;
import object.services.PostsService;
import object.services.TagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class ApiPostController {

    private PostsService postsService;
    private PostVotesService postVotesService;

    @GetMapping("/post")
    public ResponseEntity<ListPostResponseDto> getAllPosts(@RequestParam Integer offset,
                                         @RequestParam Integer limit,
                                         @RequestParam String mode){
        ListPostResponseDto dto = postsService.getListPostResponseDtoByMode(offset, limit, Mode.valueOf(mode.toUpperCase()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/search")
    public ResponseEntity<ListPostResponseDto> getPostsBySearch(@RequestParam Integer offset,
                                           @RequestParam Integer limit,
                                           @RequestParam String query){
        ListPostResponseDto dto = postsService.getListPostResponseDtoBySearch(offset, limit, query);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/{id}")
    public ResponseEntity<PostAllCommentsAndAllTagsDto> getPostById(@PathVariable Integer id){
        PostAllCommentsAndAllTagsDto dto = postsService.getPostAllCommentsAndAllTagsDto(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/byDate")
    public ResponseEntity<ListPostResponseDto> getPostsByDate(@RequestParam Integer offset,
                                         @RequestParam Integer limit,
                                         @RequestParam String date){
        ListPostResponseDto dto = postsService.getListPostResponseDtoByDate(offset, limit, date);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/byTag")
    public ResponseEntity<ListPostResponseDto> getPostsByTag(@RequestParam Integer offset,
                                        @RequestParam Integer limit,
                                        @RequestParam String tag){
        ListPostResponseDto dto = postsService.getListPostResponseDtoByTag(offset, limit, tag);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/moderation")
    public ResponseEntity<ListPostResponseDto> getPostsModeration(@RequestParam Integer offset,
                                             @RequestParam Integer limit,
                                             @RequestParam String status){
        ListPostResponseDto dto = postsService.getPostDtoModeration(offset, limit, ModerationStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/post/my")
    public ResponseEntity<ListPostResponseDto> getMyPosts(@RequestParam Integer offset,
                                     @RequestParam Integer limit,
                                     @RequestParam String status){
        ListPostResponseDto dto = postsService.getMyListPost(offset, limit, status);
        return ResponseEntity.ok(dto);
    }


    @PostMapping("/post")
    public ResponseEntity<ResultDto> addPost(@RequestBody NewPostDto request){
        ResultDto dto = postsService.addPost(request.getTime(), request.getActive(), request.getTitle(), request.getText(), request.getTags());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/post/{id}")
    public ResponseEntity<ResultDto> update(@RequestBody NewPostDto request,
                                 @PathVariable Integer id){
        ResultDto dto = postsService.update(request.getTime(), request.getActive(), request.getTitle(), request.getText(), request.getTags(), id);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/post/like")
    public ResponseEntity<ResultDto> like(@RequestBody VotesDto votesDto ){
        ResultDto dto = postVotesService.like(votesDto.getPost_id());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/post/dislike")
    public ResponseEntity<ResultDto> dislike(@RequestBody VotesDto votesDto ){
        ResultDto dto = postVotesService.disLike(votesDto.getPost_id());
        return ResponseEntity.ok(dto);
    }





}
