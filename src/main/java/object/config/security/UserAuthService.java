package object.config.security;

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
import java.awt.image.RenderedImage;
import java.io.File;
import java.util.Random;

@Service
public class UserAuthService{// implements UserDetailsService {


//
//    private UsersRepository usersRepository;
//
//    @Autowired
//    public UserAuthService(UsersRepository usersRepository) {
//        this.usersRepository = usersRepository;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
//        Users u = usersRepository.findByName(s);
//        UserAuth userAuth = new UserAuth(u.getName(), u.getPassword(), u.getEmail(), u.getPhoto());
//        return userAuth;
//    }


}
