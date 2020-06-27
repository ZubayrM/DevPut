package object.repositories;

import object.model.Users;
import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.model.Posts;
import object.services.MailSenderService;
import org.apache.coyote.Response;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostsRepository extends CrudRepository<Posts,Integer> {


    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    List<Posts> findAll(Pageable pageable);

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date and text LIKE CONCAT('%', :query, '%')")
    Optional<List<Posts>> findBySearch(String query, Pageable pageable);

    @Query(value = "FROM Posts WHERE year(time) = :year and month(time) = :month and day(time) = :day")
    List<Posts> findByDate(Integer year, Integer month, Integer day, Pageable pageable);

    ///////////////////////////////////////проблема//////////////////////////////////////////////////////
//    @Query(value = "FROM Posts p " +
//            "join Tag2Post t2p on t2p.postId = p.id " +
//            "join Tags t on t.id = t2p.tagId " +
//            "where t.name = :tag and " +
//            "p.isActive = 1 and p.moderationStatus = 'ACCEPTED' and p.time <= current_date")
//     Optional<List<Posts>> findByTag(String tag, Pageable pageable);

//    @Query(value = "select * from posts " +
//            "join tag2post on tag2post.post_id = posts.id " +
//            "join tags on tags.id = tag2post.tag_id " +
//            "where tags.name = ?1", nativeQuery = true)
//    Optional<List<Posts>> findByTag(String tag, Pageable pageable);
    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    Optional<List<Posts>> findByTag(String tag, Pageable pageable);
//////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = :status")
    Optional<List<Posts>> findByModerationStatus(ModerationStatus status, Pageable pageable);

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = :status")
    Optional<List<Posts>> findByModerationStatus(ModerationStatus status);

    @Query(value = "FROM Posts WHERE isActive = :active and moderationStatus = :status")
    Optional<List<Posts>> findAllMyPosts(Integer active, ModerationStatus status, Pageable pageable);

    //todo ПО ДРУГОМУ ПОКА НЕ ПОЛУЧАЕТСЯ...  SELECT COUNT(Posts)- не работает
    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    List<Posts> getAllPosts();

    @Query(value = "SELECT p FROM Posts p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED'  and  year(p.time) = year(:year) and p.time <= current_date", nativeQuery = true)
    Set<Posts> getYears(Date year);

    Integer countByAuthor(Users author);

    Posts findFirstByTimeAndAuthor(Date time, Users u);

    @Query(value = "SELECT sum(p.view_count) FROM Posts p", nativeQuery = true)
    Integer countByViewCount();

    @Query(value = "SELECT * FROM Posts p ORDER BY p.time LIMIT 1", nativeQuery = true)
    Posts findFirstByTime();

    Optional<List<Posts>> findByAuthor(Users u);

//    @Query(value = "SELECT p FROM Posts p WHERE  p.isActive = :active and p.moderationStatus = :status")
//    Optional<List<Posts>> getMyPosts( Integer active, ModerationStatus status, Pageable pageable);
}
