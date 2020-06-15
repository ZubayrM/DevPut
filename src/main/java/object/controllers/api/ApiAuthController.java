package object.controllers.api;

import lombok.AllArgsConstructor;
import object.dto.request.LoginDto;
import object.dto.response.CaptchaDto;
import object.dto.response.ResultDto;
import object.dto.response.StatisticsDto;
import object.dto.response.post.CalendarDto;
import object.services.CaptchaCodesService;
import object.services.PostsService;
import object.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;

@RestController
@AllArgsConstructor
public class ApiAuthController {

    private PostsService postsService;
    private UsersService usersService;
    private CaptchaCodesService captchaCodesService;


    @GetMapping("/api/calendar")
    public ResponseEntity getCalendar(@RequestParam @Nullable String year ){
        CalendarDto dto = postsService.getCalendar(year);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto){
        ResultDto dto = usersService.login(loginDto.getE_mail(), loginDto.getPassword());
        return ResponseEntity.ok(dto);
    }

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
        StatisticsDto dto = postsService.myStatistics(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/statistics/all")
    public ResponseEntity allStatistics(){
        StatisticsDto dto = postsService.allStatistic();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/auth/logout")
    public ResponseEntity logout(HttpServletRequest request){
        usersService.logout(request);
        return ResponseEntity.ok(true);
    }



}
