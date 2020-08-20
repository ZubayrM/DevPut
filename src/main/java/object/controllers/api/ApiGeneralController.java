package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.dto.request.post.RequestCommentDto;
import object.dto.request.post.ModerationPostDto;
import object.dto.request.user.MyProfileDto;
import object.dto.response.InitResponseDto;
import object.dto.response.ResultDto;
import object.dto.response.StatisticsDto;
import object.dto.response.post.CalendarDto;
import object.dto.response.resultPostComment.ResultPostCommentDto;
import object.dto.response.tag.TagsDto;
import object.model.User;
import object.services.GeneralService;
import object.services.TagsService;
import object.services.UsersService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Log4j2
public class ApiGeneralController {

    private InitResponseDto initResponseDto;
    private UsersService userService;
    private GeneralService generalService;
    private TagsService tagsService;

    @GetMapping("/init")
    public ResponseEntity<InitResponseDto> getInfo(){
        return ResponseEntity.ok(initResponseDto);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarDto> getCalendar(@RequestParam @Nullable String year ){
        CalendarDto dto = generalService.getCalendar(year);
        return ResponseEntity.ok(dto);
    }



    @GetMapping("/statistics/my")
    public ResponseEntity<StatisticsDto> myStatistics(){
        StatisticsDto dto = generalService.myStatistics();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsDto> allStatistics(){
        StatisticsDto dto = generalService.allStatistic();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/image")
    public ResponseEntity<String> addImage(@RequestBody MultipartFile image){
        String imagePath = userService.addImage(image);
        return  ResponseEntity.ok(imagePath);
    }

    @PostMapping("/comment")
    public ResponseEntity<ResultPostCommentDto> addComment(@RequestBody RequestCommentDto commentDto){
        ResultPostCommentDto dto =  generalService.addComment(commentDto.getParentId(), commentDto.getPostId(), commentDto.getText());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/tag")
    public ResponseEntity<TagsDto> getTags(@RequestParam @Nullable String query){
        TagsDto dto = tagsService.getTagByQuery(query);
        return ResponseEntity.ok(dto);
    }





}
