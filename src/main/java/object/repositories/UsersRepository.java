package object.repositories;

import object.model.Users;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<Users,Integer> {

    //Users findById(int id);

    Users findByName(String s);

    Optional<Users> findByEmail(String email);

    Optional<Users> findByCode(String code);
}
