package object.repositories;

import object.model.PostComments;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentsRepository extends CrudRepository <PostComments, Integer> {

    List<PostComments> findAllByPostId(int postId);

    Integer countByPostId(Integer postId);

}
