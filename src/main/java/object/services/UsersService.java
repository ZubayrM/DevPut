package object.services;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.config.security.MyUserDetails;
import object.dto.response.ResultDto;
import object.dto.response.UserResponseDto;
import object.dto.response.auth.AuthUserResponseDto;
import object.dto.response.auth.UserAuthDto;
import object.dto.response.errors.ErrorsAuthDto;
import object.dto.response.errors.ErrorsMessageDto;
import object.dto.response.errors.ErrorsRegisterDto;
import object.model.CaptchaCodes;
import object.model.Users;
import object.model.enums.ModerationStatus;
import object.repositories.CaptchaCodesRepository;
import object.repositories.PostsRepository;
import object.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;


@Service
@Log4j2
public class UsersService {

    @Autowired
    AuthenticationManager authenticationManager;

    private final String PATH_TO_IMAGE = "upload/";

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private MailSenderService mailSenderService;

    @Autowired
    private CaptchaCodesRepository captchaCodesRepository;

    @SneakyThrows
    public String addImage(Image image) {
        String newPath = generatePathImage();
        ImageIO.write((RenderedImage) image, "png", new File(newPath));
        return newPath;
    }



    public ResultDto login(String email, String password) {
        Optional<Users> user = usersRepository.findByEmail(email);

        if (user.isPresent()){
            if (user.get().getPassword().equals(password)) {

                UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(email, password);
                Authentication authentication = authenticationManager.authenticate(authRequest);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                return new AuthUserResponseDto(generatedUserAuth(user.get()));
            } else
                return new ResultDto(false);
        } else
            return new ResultDto(false);
    }

    public AuthUserResponseDto check() {

            if (getUser()!= null)
                return new AuthUserResponseDto(generatedUserAuth(getUser()));
            else
                return new AuthUserResponseDto(false);

    }

    public ResultDto restore(String email) {
        Optional<Users> user = usersRepository.findByEmail(email);
        if (user.isPresent()){
            String token = UUID.randomUUID().toString();
            user.get().setCode(token);
            String url = "http://localhost:8080/login/change-password/" + user.get().getCode();
            String message = String.format("Для восстановления пароля перейдите по ссылке %s", url );
            mailSenderService.send(user.get().getEmail(), "Password recovery", message);
            return new ResultDto(true);
        } else
            return new ResultDto(false);
    }

    private UserAuthDto generatedUserAuth(Users user) {
        return UserAuthDto.builder()
                .id(user.getId())
                .name(user.getName())
                .photo(user.getPhoto())
                .email(user.getEmail())
                .moderation(user.getIsModerator() > 0)
                .moderationCount(user.getIsModerator() > 0 ? postsRepository.findByModerationStatus(ModerationStatus.NEW).get().size() : 0)
                .settings(user.getIsModerator() > 0)
                .build();
    }

    private String generatePathImage() {
        String dir1 = getRandomPath();
        String dir2 = getRandomPath();
        String dir3 = getRandomPath();
        String image = UUID.randomUUID().toString().substring(0,3).concat("/");
        return "/img/unload" + dir1 + dir2 + dir3 + image + ".png";
    }

    private String getRandomPath() {
        return UUID.randomUUID().toString().substring(0,2).concat("/");
    }

    public ResultDto password(String code, String password, String captcha, String captchaSecret) {
        Optional<Users> user = usersRepository.findByCode(code);
        if (user.isPresent()){
            CaptchaCodes captchaCodes = captchaCodesRepository.findByCode(captcha);
            if (captchaCodes.getSecretCode().equals(captchaSecret)){
                if (password.length() > 6 ) {
                    user.get().setPassword(password);
                    return new ResultDto(true);
                } else return new ErrorsMessageDto<>(new ErrorsAuthDto(null, "Пароль короче 6-ти символов", null), false);
            } else
                return new ErrorsMessageDto<>(new ErrorsAuthDto(null, null,"Код с картинки введён неверно"), false);
        } else
            return new ErrorsMessageDto<>(
                new ErrorsAuthDto("Ссылка для восстановления пароля устарела.\n" +
                "<a href=http://localhost:8080/auth/restore>Запросить ссылку снова</a>", null, null),false);

    }

    public ResultDto register(String email, String password, String captcha, String captchaSecret) {
        Optional<Users> user = usersRepository.findByEmail(email);

        if (!user.isPresent()){
           // if (name.split(" ").length == 2){
                if (password.length() >= 6){
                    CaptchaCodes byCode = captchaCodesRepository.findByCode(captchaSecret);
                    if (byCode != null){
                    if (byCode.getSecretCode().equals(captcha)){
                        return generatedNewUser(email, password);
                    } else
                        return new ErrorsMessageDto<>(new ErrorsRegisterDto(null, null, null, "Код с картинки введён неверно",null),false);
                    }else
                        return new ErrorsMessageDto<>(new ErrorsRegisterDto(null, null, null, "Код с картинки введён неверно",null),false);
                } else
                    return new ErrorsMessageDto<>(new ErrorsRegisterDto(null, null,  "Пароль короче 6-ти символов", null,null),false);
//            } else
//                return new ErrorsMessageDto<>(new ErrorsRegisterDto(null, "Имя указано неверно", null, null,null),false);
        } else
            return new ErrorsMessageDto<>(new ErrorsRegisterDto( "Этот e-mail уже зарегистрирован", null, null, null,null),false);
    }

    private ResultDto generatedNewUser( String email, String password) {
        Users user = new Users();
        int index = email.indexOf("@");
        user.setName(email.substring(0,index));
        user.setEmail(email);
        user.setPassword(password);
        user.setRegTime(new Date());
        user.setIsModerator(0);
        usersRepository.save(user);
        return new ResultDto(true);
    }

    public ResultDto profileMy(String photo, Integer removePhoto, String name, String email, String password, HttpServletRequest request) {
        String userByEmail = request.getHeader("email");///

        Optional<Users> user = usersRepository.findByEmail(userByEmail);
        if (user.isPresent()) {
            Users u = user.get();

            if (photo != null && photo.length() > 5) {
                u.setPhoto(photo);
            } else return new ErrorsMessageDto<>( new ErrorsRegisterDto(null, null, null, null, "Фото слишком большое, нужно не более 5 Мб"), false);


            if (name.split(" ").length > 1) {
                u.setName(name);
            } else return new ErrorsMessageDto<>( new ErrorsRegisterDto(null, "Имя указано неверно", null, null, null), false);


            if (!usersRepository.findByEmail(email).isPresent()) {
                u.setEmail(email);
            } else return new ErrorsMessageDto<>( new ErrorsRegisterDto("Этот e-mail уже зарегистрирован", null, null, null, null), false);


            if (password.length() > 6) {
                u.setPassword(password);
            } else return new ErrorsMessageDto<>( new ErrorsRegisterDto(null, null, "Пароль короче 6-ти символов", null, null), false);

            return new ResultDto(true);


        }

        return  new ResultDto(true);
    }

    @SneakyThrows
    public void logout(HttpServletRequest request) {
        request.logout();
        request.getSession(false);
    }

    public Users getUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String userEmail = ((MyUserDetails) principal).getEmail();
            log.info(userEmail + " мои молитвы услышанны");
            Optional<Users> user = usersRepository.findByEmail(userEmail);
            return user.orElse(null);
        }
        else return null;
    }

    public UserResponseDto getUserDto(){
        Users s = getUser();
        return new UserResponseDto(s.getId(), s.getName());
    }
}
