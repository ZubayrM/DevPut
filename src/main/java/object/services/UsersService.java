package object.services;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import object.component.ImagePath;
import object.config.security.MyUserDetails;
import object.dto.request.user.MyProfileDto;
import object.dto.response.ResultDto;
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
import object.services.Component.MailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;


@Service
@Log4j2
@AllArgsConstructor
public class UsersService {


    private AuthenticationManager authenticationManager;
    private UsersRepository usersRepository;
    private PostsRepository postsRepository;
    private MailSender mailSender;
    private CaptchaCodesRepository captchaCodesRepository;
    private ImagePath imagePath;

    @SneakyThrows
    public String addImage(MultipartFile image) {
        int i = image.getContentType().indexOf("/");
        String type = image.getContentType().substring(i + 1);

        String imageName = UUID.randomUUID().toString().substring(0,3).concat("." + type);

        List<String> list = generatePathImage();

        String newPath = list.get(0).concat(imageName);
        BufferedImage bi = ImageIO.read(image.getInputStream());
        ImageIO.write(bi, type, new File(newPath));

        log.info(list.get(0) + list.get(1));

        return list.get(1).concat(imageName);
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
            mailSender.send(user.get().getEmail(), "Password recovery", message);
            return new ResultDto(true);
        } else
            return new ResultDto(false);
    }

    private UserAuthDto generatedUserAuth(Users user) {
        return UserAuthDto.builder()
                .id(user.getId())
                .name(user.getName())
                .photo(imagePath.getImage() + "?email=" + user.getEmail())
                .email(user.getEmail())
                .moderation(user.getIsModerator() > 0)
                .moderationCount(user.getIsModerator() > 0 ? postsRepository.findByModerationStatus(ModerationStatus.NEW).get().size() : 0)
                .settings(user.getIsModerator() > 0)
                .build();
    }

    @SneakyThrows
    private List<String> generatePathImage() {
        String path = "/img/unload/";
        String absolutePath = "src/main/resources/static/img/unload/";

        String dir1 = getRandomPath();
        String dir2 = getRandomPath();
        String dir3 = getRandomPath();

        path += (dir1 + dir2 + dir3);
        absolutePath += (dir1 + dir2 + dir3);

        File dir = new File(absolutePath);
        dir.mkdirs();

        return Arrays.asList(absolutePath, path);
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
                    usersRepository.save(user.get());
                    return new ResultDto(true);
                } else return new ErrorsMessageDto<>(new ErrorsAuthDto(null, "Пароль короче 6-ти символов", null), false);
            } else
                return new ErrorsMessageDto<>(new ErrorsAuthDto(null, null,"Код с картинки введён неверно"), false);
        } else
            return new ErrorsMessageDto<>(
                new ErrorsAuthDto("Ссылка для восстановления пароля устарела.\n" +
                "<a href=http://localhost:8080/auth/restore>Запросить ссылку снова</a>", null, null),false);

    }

    public ResultDto register(String email, String password, String captcha, String captchaSecret, String name) {
        Optional<Users> user = usersRepository.findByEmail(email);

        if (!user.isPresent()){
           // if (name.split(" ").length == 2){
                if (password.length() >= 6){
                    CaptchaCodes byCode = captchaCodesRepository.findByCode(captchaSecret);
                    if (byCode != null){
                    if (byCode.getSecretCode().equals(captcha)){
                        return generatedNewUser(email, password, name);
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

    private ResultDto generatedNewUser( String email, String password, String name) {
        Users user = new Users();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRegTime(new Date());
        user.setIsModerator(0);
        usersRepository.save(user);
        return new ResultDto(true);
    }

    @SneakyThrows
    public ResultDto updateProfile(MyProfileDto dto) {
        Users u = getUser();

        if (dto.getPhoto() != null) {
            if (dto.getPhoto().getSize() < 50_000)
                u.setPhoto(Base64.getEncoder().encodeToString(dto.getPhoto().getBytes()));
            else
                return new ErrorsMessageDto<>(new ErrorsRegisterDto(null, null, null, null, "Фото слишком большое, нужно не более 5 Мб"), false);
        }


        if (dto.getName() != null) {
            if (dto.getName().length() > 1) {
                u.setName(dto.getName());
            } else
                return new ErrorsMessageDto<>(new ErrorsRegisterDto(null, "Имя указано неверно", null, null, null), false);
        }

        if (dto.getEmail() != null) {
            if (!usersRepository.findByEmail(dto.getEmail()).isPresent() || dto.getEmail().equals(getUser().getEmail())) {
                u.setEmail(dto.getEmail());
            } else
                return new ErrorsMessageDto<>(new ErrorsRegisterDto("Этот e-mail уже зарегистрирован", null, null, null, null), false);
        }

        if (dto.getPassword() != null) {
            if (dto.getPassword().length() > 6) {
                u.setPassword(dto.getPassword());
            } else
                return new ErrorsMessageDto<>(new ErrorsRegisterDto(null, null, "Пароль короче 6-ти символов", null, null), false);
        }

        usersRepository.save(u);

        return new ResultDto(true);

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

    public Users getUser(String email){
        return usersRepository.findByEmail(email).orElse(null);
    }
}
