package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import object.dto.response.CaptchaDto;
import object.dto.response.MyStatisticsDto;
import object.dto.response.ResultDto;
import object.dto.response.auth.AuthUserResponseDto;
import object.dto.response.post.CalendarDto;
import object.model.Users;
import object.services.CaptchaCodesService;
import object.services.PostsService;
import object.services.UsersService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

@RestController
@AllArgsConstructor
public class ApiAuthController {

    private PostsService postsService;
    private UsersService usersService;
    private CaptchaCodesService captchaCodesService;


    @GetMapping("/api/calendar")
    public ResponseEntity getCalendar(@RequestParam String year ){
        CalendarDto dto = postsService.getCalendar(year);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity login(@RequestParam Map<String, Integer> id){
        System.out.println(id);
        return ResponseEntity.ok(null);
    }
//    public ResponseEntity login (@RequestParam String email,
//                                 @RequestParam String password){
//        ResultDto dto = usersService.login(email, password);
//        return ResponseEntity.ok(dto);
//    }

    @GetMapping("/api/auth/check")
    public ResponseEntity check(HttpServletRequest request){
        ResultDto dto = usersService.check(request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/auth/restore")
    public ResponseEntity restore(@RequestParam String email){
        ResultDto dto = usersService.restore(email);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/auth/password")
    public ResponseEntity upPassword(@RequestParam String code,
                                     @RequestParam String password,
                                     @RequestParam String captcha,
                                     @RequestParam("captcha_secret") String captchaSecret){
        ResultDto dto = usersService.password(code, password, captcha, captchaSecret);
        return null;
    }

    @PostMapping("/api/auth/register")
    public ResponseEntity register(@RequestParam("e_mail") String email,
                                   @RequestParam String name,
                                   @RequestParam String password,
                                   @RequestParam String captcha,
                                   @RequestParam("captcha_secret") String captchaSecret){
        ResultDto dto = usersService.register(email,name,password,captcha,captchaSecret);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/profile/my")
    public ResponseEntity profileMy(@RequestParam @Nullable String photo,
                                 @RequestParam Integer removePhoto,
                                 @RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String password, HttpServletRequest request){
        ResultDto dto = usersService.profileMy(photo, removePhoto, name, email, password, request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/auth/captcha")
    public ResponseEntity captcha(){
        CaptchaDto dto = captchaCodesService.captcha();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/statistics/my")
    public ResponseEntity myStatistics(HttpServletRequest request){
        MyStatisticsDto dto = postsService.myStatistics(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity allStatistics(){
        return null;
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity logout(){
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }



}
