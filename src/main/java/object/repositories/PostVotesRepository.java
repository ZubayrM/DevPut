package object.repositories;

import object.model.PostVotes;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVotes,Integer> {

    List<PostVotes> findByPostIdAndValue(int postId, int value);

    Integer countByPostIdAndValue(Integer postId, Integer value);

}
