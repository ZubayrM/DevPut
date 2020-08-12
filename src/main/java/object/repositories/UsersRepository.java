package object.repositories;

import object.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends CrudRepository<User,Integer> {

    @Query(value = "FROM User WHERE email = :email")
    Optional<User> findByEmail(String email);

    @Query(value = "FROM User WHERE code = :code")
    Optional<User> findByCode(String code);
}
