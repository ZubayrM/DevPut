package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.request.post.ModerationPostDto;
import object.dto.request.user.MyProfileDto;
import object.dto.response.InitResponseDto;
import object.dto.response.ResultDto;
import object.dto.response.StatisticsDto;
import object.dto.response.post.CalendarDto;
import object.services.GlobalSettingsService;
import object.services.PostsService;
import object.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.io.BufferedReader;
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
    private PostsService postsService;
    private UsersService userService;
    private GlobalSettingsService globalSettingsService;

    @GetMapping("/init")
    public ResponseEntity getInfo(){
        return ResponseEntity.ok(initResponseDto);
    }

    @GetMapping("/calendar")
    public ResponseEntity getCalendar(@RequestParam @Nullable String year ){
        CalendarDto dto = postsService.getCalendar(year);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/profile/my")
    public ResponseEntity profileMy(@RequestBody MyProfileDto request){
        ResultDto dto = userService.updateProfile(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/statistics/my")
    public ResponseEntity myStatistics(){
        StatisticsDto dto = postsService.myStatistics();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/statistics/all")
    public ResponseEntity allStatistics(){
        StatisticsDto dto = postsService.allStatistic();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/image")
    public ResponseEntity addImage(Image image){
        String imagePath = userService.addImage(image);
        return  ResponseEntity.ok(imagePath);
    }

    @GetMapping("/image")
    public ResponseEntity getImage(){
        Image image;
         Base64.getDecoder().decode(userService.getUser().getPhoto());
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
