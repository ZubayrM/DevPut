package object.repositories;

import object.model.Tags;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends CrudRepository<Tags, Integer> {

    @Query(value = "FROM Tags where name = :name")
    Optional<Tags> findByName(String name);


    @Query(value = "FROM Tags WHERE  name LIKE concat(:query, '%')")
    List<Tags> findAllByName(String query);

    @Query(value = "FROM Tags")
    List<Tags> findAll();
}
