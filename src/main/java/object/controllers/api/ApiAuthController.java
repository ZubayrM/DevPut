package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.request.auth.LoginDto;
import object.dto.request.auth.RegisterDto;
import object.dto.response.CaptchaDto;
import object.dto.response.ResultDto;
import object.dto.response.StatisticsDto;
import object.dto.response.post.CalendarDto;
import object.services.CaptchaCodesService;
import object.services.PostsService;
import object.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@AllArgsConstructor
@Log4j2
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
        ResultDto dto = usersService.login(loginDto.getEMail(), loginDto.getPassword());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/api/auth/check")
    public ResponseEntity check(){
        ResultDto dto = usersService.check();
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
    public ResponseEntity register(@RequestBody RegisterDto request){
        ResultDto dto = usersService.register(request.getEMail(), request.getPassword(), request.getCaptcha(), request.getCaptchaSecret());
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
