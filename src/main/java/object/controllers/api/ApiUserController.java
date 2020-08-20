package object.controllers.api;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import object.dto.request.user.MyProfileDto;
import object.dto.response.ResultDto;
import object.services.UsersService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile/my")
@AllArgsConstructor
@Log4j2
public class ApiUserController {

    UsersService usersService;

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ResultDto> profileMyPhoto(@ModelAttribute MyProfileDto request){
        ResultDto dto = usersService.updateProfile(request);
        return ResponseEntity.ok(dto);
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<ResultDto> profileMy(@RequestBody MyProfileDto request){
        ResultDto dto = usersService.updateProfile(request);
        return ResponseEntity.ok(dto);
    }
}
