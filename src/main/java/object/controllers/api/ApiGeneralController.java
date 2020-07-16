package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.component.ImagePath;
import object.dto.request.post.ModerationPostDto;
import object.dto.request.user.MyProfileDto;
import object.dto.response.InitResponseDto;
import object.dto.response.ResultDto;
import object.dto.response.StatisticsDto;
import object.dto.response.post.CalendarDto;
import object.model.Users;
import object.services.GlobalSettingsService;
import object.services.PostsService;
import object.services.UsersService;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
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
    private ImagePath imagePath;

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
    public ResponseEntity profileMy(@ModelAttribute MyProfileDto request){
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
    public ResponseEntity addImage(@RequestBody MultipartFile image){
        String imagePath = userService.addImage(image);
        return  ResponseEntity.ok(imagePath);
    }

    @SneakyThrows
    @GetMapping("/image")
    public ResponseEntity getImage(@RequestParam String email){
        Users user = userService.getUser(email);
        byte[] img;

        if (user.getPhoto() != null)
        img = Base64.getDecoder().decode(user.getPhoto());
        else img = IOUtils.toByteArray(new ClassPathResource("static/img/default-1.png").getInputStream());

        return ResponseEntity.ok().body(img);
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
        return ResponseEntity.ok(globalSettingsService.getSetting());
    }

    @PutMapping("/settings")
    public ResponseEntity setSettings(@RequestBody Map<String, Boolean> globalSetting){
        if (userService.getUser().getIsModerator() > 0) {
            log.info(globalSetting.keySet() + " " + globalSetting.values());
            globalSettingsService.saveSettings(globalSetting);
        }
        return ResponseEntity.ok(HttpStatus.ACCEPTED);
    }







}
