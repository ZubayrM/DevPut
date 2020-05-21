package object.repositories;

import object.model.Tag2Post;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {


    //не работает COUNT
//    @Query(value = "SELECT COUNT(t2p) FROM Tag2Post t2p WHERE t2p.tagId = :id")
//    Integer countByTag(Integer id);

    @Query(value = "FROM Tag2Post t2p WHERE t2p.tagId = :id ")
    List<Tag2Post> countByTag(Integer id);
}
