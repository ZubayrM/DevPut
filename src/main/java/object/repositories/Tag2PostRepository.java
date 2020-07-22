package object.repositories;

import object.model.Tag2Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {



    @Query(value = "SELECT COUNT(*) FROM Tag2Post WHERE tag_id = ?1", nativeQuery = true)
    Integer countByTag(Integer id);

    @Query(value = "SELECT COUNT(*) FROM Tag2Post", nativeQuery = true)
    Integer countTag2Post();
}
