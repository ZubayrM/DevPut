package object.services;

import lombok.SneakyThrows;
import object.model.Users;
import object.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Random;


@Service
public class UsersService {

    private final String PATH_TO_IMAGE = "upload/";

    @Autowired
    private UsersRepository usersRepository;

    public Users getUser() {
        return usersRepository.findById(1);
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
        return result + "/img.png";
    }
}
