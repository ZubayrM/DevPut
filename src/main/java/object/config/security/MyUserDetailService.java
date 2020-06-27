package object.config.security;

import object.model.Users;
import object.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;


    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<Users> user = usersRepository.findByEmail(s);
        Users u;
        if (user.isPresent()){
            u = user.get();
            return new MyUserDetails(u.getEmail(), u.getPassword(), u.getName() , u.getIsModerator());

        } else
            return null;
    }


}
