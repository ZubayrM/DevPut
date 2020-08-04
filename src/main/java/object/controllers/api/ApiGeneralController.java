package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.dto.request.post.CommentDto;
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

    @PostMapping("/profile/my")
    public ResponseEntity<ResultDto> profileMy(@ModelAttribute MyProfileDto request){
        ResultDto dto = userService.updateProfile(request);
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

    @SneakyThrows
    @GetMapping("/image")
    @Deprecated
    public ResponseEntity<byte[]> getImage(@RequestParam String email){
        User user = userService.getUser(email);
        byte[] img;

        if (user.getPhoto() != null)
        img = Base64.getDecoder().decode(user.getPhoto());
        else img = IOUtils.toByteArray(new ClassPathResource("static/img/default-1.png").getInputStream());

        return ResponseEntity.ok(img);
    }


    @PostMapping("/moderation")
    public ResponseEntity<Map<String,String>> moderation(@RequestBody ModerationPostDto request){
        generalService.moderationPost(request.getPostId(),request.getDecision());
        Map<String, String> m = new HashMap<>();
        m.put("data", new Date().toString());
        return ResponseEntity.ok(m);
    }


    @GetMapping("/settings")
    public ResponseEntity<Map<String,String>> getSettings(){
        return ResponseEntity.ok(generalService.getSetting());
    }

    @PutMapping("/settings")
    public ResponseEntity<HttpStatus> setSettings(@RequestBody Map<String, Boolean> globalSetting){
        if (userService.getUser().getIsModerator() > 0) {
            log.info(globalSetting.keySet() + " " + globalSetting.values());
            generalService.saveSettings(globalSetting);
        }
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }

    @PostMapping("/comment")
    public ResponseEntity<ResultPostCommentDto> addComment(@RequestBody CommentDto commentDto){
        ResultPostCommentDto dto =  generalService.addComment(commentDto.getParentId(), commentDto.getPostId(), commentDto.getText());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/tag")
    public ResponseEntity<TagsDto> getTags(@RequestParam @Nullable String query){
        TagsDto dto = tagsService.getTagByQuery(query);
        return ResponseEntity.ok(dto);
    }





}
