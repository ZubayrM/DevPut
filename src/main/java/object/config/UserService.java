package object.config;

import lombok.SneakyThrows;
import object.model.Users;
import object.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Random;

@Service
public class UserService implements UserDetailsService {

    private final String PATH_TO_IMAGE = "upload/";

    private UsersRepository usersRepository;

    @Autowired
    public UserService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Users u = usersRepository.findByName(s);
        UserAuth userAuth = new UserAuth(u.getName(), u.getPassword(), u.getEmail(), u.getPhoto());
        return userAuth;
    }

    @SneakyThrows
    public String addImage(Image image) {
        String newPath = generatePathImage();
        ImageIO.write((RenderedImage) image, "png", new File(newPath));
        return newPath;
    }

    private String generatePathImage() {
        Random r = new Random();
        char[] c = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
        int length = c.length;
        String result = PATH_TO_IMAGE;
        for (int i = 0; i < 4; i++) {
            if (i == 3) result += "/";
            else result += c[r.nextInt(length)];
        }
        return result;
    }
}
