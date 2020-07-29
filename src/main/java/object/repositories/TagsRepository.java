package object.repositories;

import object.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends CrudRepository<Tag, Integer> {

    @Query(value = "FROM Tag where name = :name")
    Optional<Tag> findByName(String name);


    @Query(value = "FROM Tag WHERE  name LIKE concat(:query, '%')")
    List<Tag> findAllByName(String query);

    @Query(value = "FROM Tag")
    List<Tag> findAll();

    @Query(value = "SELECT COUNT(*) FROM tags", nativeQuery = true)
    Integer countTags();
}
