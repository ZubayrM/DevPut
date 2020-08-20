package object.repositories;

import object.model.Tag;
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

    @Query(value = "SELECT t.name, COUNT(t.name) / (SELECT COUNT(t.name) FROM tag2post t2p JOIN posts p ON t2p.post_id = p.id JOIN tags t ON t2p.tag_id = t.id WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time <= current_date GROUP BY t.name ORDER BY COUNT(t.name) DESC LIMIT 1) " +
            "FROM tag2post t2p JOIN posts p ON t2p.post_id = p.id JOIN tags t ON t2p.tag_id = t.id " +
            "WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time <= current_date " +
            "GROUP BY t.name", nativeQuery = true)
    List<Object[]> getAll();

    @Query(value = "SELECT t.name, COUNT(t.name) / (SELECT COUNT(t.name) FROM tag2post t2p JOIN posts p ON t2p.post_id = p.id JOIN tags t ON t2p.tag_id = t.id WHERE p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time <= current_date GROUP BY t.name ORDER BY COUNT(t.name) DESC LIMIT 1)  " +
            "FROM tag2post t2p JOIN posts p ON t2p.post_id = p.id JOIN tags t ON t2p.tag_id = t.id " +
            "WHERE LIKE CONCAT(?1, '%') and p.is_active = 1 and p.moderation_status = 'ACCEPTED' and p.time <= current_date " +
            "GROUP BY t.name", nativeQuery = true)
    List<Object[]> getAllByQuery(String query);
}
