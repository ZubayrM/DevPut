package object.controllers.api;

import object.model.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import java.time.LocalDate;
import java.util.Date;

@RestController
public class ApiAuthController {

    @GetMapping("/api/calendar/")
    public ResponseEntity getCalendar(Date date){
        return null;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity entrance(@RequestParam("e_mail") String email,
                                   @RequestParam String password, ServletRequest request){
        return ResponseEntity.ok(null);
    }

    @GetMapping("/api/auth/check/")
    public ResponseEntity check(Users user){
        return null;
    }

    @PostMapping("/api/auth/restore/")
    public ResponseEntity restore(){
        return null;
    }

    @PostMapping("/api/auth/password/")
    public ResponseEntity upPassword(String code, String password, int captcha, String captchaSecret){
        return null;
    }

    @PostMapping("/api/auth/register/")
    public ResponseEntity register(String email, String name, String password, int captcha, String captchaSecret){
        return null;
    }

    @PostMapping("/api/profile/my/")
    public ResponseEntity upUser(String pathPhoto, int removePhoto, String name, String email, String password){
        return null;
    }

    @GetMapping("/api/auth/captcha")
    public ResponseEntity captcha(){
        return null;
    }

    @GetMapping("/api/statistics/my/")
    public ResponseEntity myStatistics(Users user){
        return null;
    }

    @GetMapping("/api/statistics/all/")
    public ResponseEntity allStatistics(){
        return null;
    }

    @GetMapping("/api/auth/logout/")
    public ResponseEntity logout(){
        return ResponseEntity.status(HttpStatus.OK).body(true);
    }



}
