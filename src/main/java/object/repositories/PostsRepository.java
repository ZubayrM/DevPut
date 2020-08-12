package object.repositories;

import object.model.Post;
import object.model.User;
import object.model.enums.ModerationStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PostsRepository extends CrudRepository<Post,Integer> {


    @Query(value = "FROM Post WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_time")
    List<Post> findAll(Pageable pageable);

    @Query(value = "FROM Post WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date and text LIKE CONCAT('%', :query, '%')")
    Optional<List<Post>> findBySearch(String query, Pageable pageable);

    @Query(value = "FROM Post WHERE year(time) = :year and month(time) = :month and day(time) = :day and isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    List<Post> findByDate(Integer year, Integer month, Integer day, Pageable pageable);

    @Query(value = "FROM Post WHERE id = :id")
    Optional<Post> findById(Integer id);

    @Query(value = "FROM Post WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    Optional<List<Post>> findByTag(String tag, Pageable pageable);

    @Query(value = "FROM Post WHERE isActive = 1 and moderationStatus = :status")
    Optional<List<Post>> findByModerationStatus(ModerationStatus status, Pageable pageable);

    @Query(value = "FROM Post WHERE isActive = 1 and moderationStatus = :status")
    Optional<List<Post>> findByModerationStatus(ModerationStatus status);

//    @Query(value = "FROM Post WHERE isActive = :active and moderationStatus = :status")
//    Optional<List<Post>> findAllMyPosts(Integer active, ModerationStatus status, Pageable pageable);

    @Query(value = "FROM Post WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    List<Post> getAllPosts();

    @Query(value = "FROM Post WHERE isActive = :active and moderationStatus = :moderationStatus and author = :user and time <= current_date")
    List<Post> getAllPosts(Integer active, ModerationStatus moderationStatus, User user, Pageable pageable);

//    @Query(value = "SELECT p FROM Posts p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED'  and  year(p.time) = year(:year) and p.time <= current_date", nativeQuery = true)
//    Set<Post> getYears(Date year);

    @Query(value = "SELECT count(*) from posts where user_id = ?1", nativeQuery = true)
    Integer countByAuthor(Integer authorId);

    @Query(value = "SELECT * FROM posts WHERE user_id = ?1 ORDER BY time ASC LIMIT 1", nativeQuery = true)
    Post findFirstByTimeToAuthor(Integer userId);

    @Query(value = "SELECT sum(p.view_count) FROM posts p", nativeQuery = true)
    Integer countByViewCount();

    @Query(value = "SELECT * FROM posts p ORDER BY p.time LIMIT 1", nativeQuery = true)
    Post findFirstByTime();

    @Query(value = "SELECT SUM(p.view_count) FROM posts p WHERE p.user_id = ?1 AND p.moderation_status = 'ACCEPTED' AND p.time <= current_date", nativeQuery = true)
    Integer countViews(Integer authorId);

    @Query(value = "SELECT COUNT(*) FROM posts WHERE moderation_status = 'ACCEPTED' AND time <= current_date", nativeQuery = true)
    Integer countPosts();

}
