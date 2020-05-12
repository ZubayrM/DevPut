package object.services;

import object.model.Users;
import object.repositories.UsersRepository;
import org.springframework.stereotype.Service;


@Service
public class UsersService {

    private UsersRepository usersRepository;

    public Users getUser() {
        return usersRepository.findById(1);
    }
}
