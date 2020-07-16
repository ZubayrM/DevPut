package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.request.auth.LoginDto;
import object.dto.request.auth.RegisterDto;
import object.dto.request.user.MyProfileDto;
import object.dto.response.CaptchaDto;
import object.dto.response.ResultDto;
import object.dto.response.auth.AuthUserResponseDto;
import object.services.CaptchaCodesService;
import object.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
@Log4j2
@RequestMapping("/api/auth")
public class ApiAuthController {

    private UsersService usersService;
    private CaptchaCodesService captchaCodesService;


    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDto loginDto){
        ResultDto dto = usersService.login(loginDto.getEMail(), loginDto.getPassword());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/check")
    public ResponseEntity check(){
        AuthUserResponseDto dto = usersService.check();
        if (dto.getUser() != null)
            log.info(dto.getUser().getEmail());
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/restore")
    public ResponseEntity restore(@RequestParam String email){
        ResultDto dto = usersService.restore(email);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/password")
    public ResponseEntity updatePassword(@RequestParam String code,
                                         @RequestParam String password,
                                         @RequestParam String captcha,
                                         @RequestParam("captcha_secret") String captchaSecret){
        ResultDto dto = usersService.password(code, password, captcha, captchaSecret);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/register")
    public ResponseEntity register(@RequestBody RegisterDto request){
        ResultDto dto = usersService.register(request.getEMail(), request.getPassword(), request.getCaptcha(), request.getCaptchaSecret(), request.getName());
        return ResponseEntity.ok(dto);
    }




    @GetMapping("/captcha")
    public ResponseEntity captcha(){
        CaptchaDto dto = captchaCodesService.captcha();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){
        usersService.logout(request);
        return ResponseEntity.ok(true);
    }



}
