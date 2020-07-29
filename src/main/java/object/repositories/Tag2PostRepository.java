package object.repositories;

import object.model.Tag2Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {



    @Query(value = "SELECT COUNT(*) FROM tag2post WHERE tag_id = ?1", nativeQuery = true)
    Integer countByTag(Integer id);

    @Query(value = "SELECT COUNT(*) FROM tag2post", nativeQuery = true)
    Integer countTag2Post();
}
