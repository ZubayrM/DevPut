package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.request.auth.LoginDto;
import object.dto.request.auth.RegisterDto;
import object.dto.response.CaptchaDto;
import object.dto.response.ResultDto;
import object.dto.response.auth.AuthUserResponseDto;
import object.services.GeneralService;
import object.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

@RestController
@AllArgsConstructor
@Log4j2
@RequestMapping("/api/auth")
public class ApiAuthController {

    private UsersService usersService;
    private GeneralService generalService;


    @PostMapping("/login")
    public ResponseEntity<ResultDto> login(@RequestBody LoginDto loginDto){
        ResultDto dto = usersService.login(loginDto.getEMail(), loginDto.getPassword());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/check")
    public ResponseEntity<AuthUserResponseDto> check(){
        AuthUserResponseDto dto = usersService.check();
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/restore")
    public ResponseEntity<ResultDto> restore(@RequestBody String email){
        ResultDto dto = usersService.restore(email);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/password")
    public ResponseEntity<ResultDto> updatePassword(@RequestParam String code,
                                         @RequestParam String password,
                                         @RequestParam String captcha,
                                         @RequestParam("captcha_secret") String captchaSecret){
        ResultDto dto = usersService.password(code, password, captcha, captchaSecret);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/register")
    public ResponseEntity<ResultDto> register(@RequestBody RegisterDto request){
        ResultDto dto = usersService.register(request.getEMail(), request.getPassword(), request.getCaptcha(), request.getCaptchaSecret(), request.getName());
        return ResponseEntity.ok(dto);
    }

    @Transactional
    @GetMapping("/captcha")
    public ResponseEntity<CaptchaDto> captcha(){
        CaptchaDto dto = generalService.getCaptcha();
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        usersService.logout(request);
        return ResponseEntity.ok(true);
    }



}
