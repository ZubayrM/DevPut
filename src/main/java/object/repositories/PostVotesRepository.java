package object.repositories;

import object.model.PostVotes;
import object.model.Posts;
import object.model.Users;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes,Integer> {

    List<PostVotes> findByPostIdAndValue(int postId, int value);

    Integer countByPostIdAndValue(Integer postId, Integer value);

    Integer countByUserIdAndValue(Integer userId, Integer value);

    @Query(value = "SELECT COUNT(pv.id) FROM Post_votes pv WHERE pv.value = ?1 ", nativeQuery = true)
    Integer countByValue(Integer value);

    Optional<PostVotes> findByPostAndUserId(Posts p, Integer userId);
}
