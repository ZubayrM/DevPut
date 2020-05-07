package object.repositories;

import object.model.Tags;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagsRepository extends CrudRepository<Tags, Integer> {

    Optional<Tags> findByName(String name);

    List<String> findAllById(Integer[] tagIds);
}
