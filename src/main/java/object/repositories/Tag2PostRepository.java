package object.repositories;

import object.model.Tag2Post;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface Tag2PostRepository extends CrudRepository<Tag2Post, Integer> {

    List<Tag2Post> findAllByTagId(Integer[] tagId);

    List<Tag2Post> findAllByPostId(Integer id);
}
