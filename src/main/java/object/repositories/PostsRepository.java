package object.repositories;

import object.model.Posts;
import object.model.Users;
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
public interface PostsRepository extends CrudRepository<Posts,Integer> {


    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    List<Posts> findAll(Pageable pageable);

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date and text LIKE CONCAT('%', :query, '%')")
    Optional<List<Posts>> findBySearch(String query, Pageable pageable);

    @Query(value = "FROM Posts WHERE year(time) = :year and month(time) = :month and day(time) = :day and isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    List<Posts> findByDate(Integer year, Integer month, Integer day, Pageable pageable);

    @Query(value = "FROM Posts WHERE id = :id")
    Optional<Posts> findById(Integer id);

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    Optional<List<Posts>> findByTag(String tag, Pageable pageable);

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = :status")
    Optional<List<Posts>> findByModerationStatus(ModerationStatus status, Pageable pageable);

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = :status")
    Optional<List<Posts>> findByModerationStatus(ModerationStatus status);

    @Query(value = "FROM Posts WHERE isActive = :active and moderationStatus = :status")
    Optional<List<Posts>> findAllMyPosts(Integer active, ModerationStatus status, Pageable pageable);

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    List<Posts> getAllPosts();

    @Query(value = "FROM Posts WHERE isActive = :active and moderationStatus = :moderationStatus and author = :user and time <= current_date")
    List<Posts> getAllPosts(Integer active, ModerationStatus moderationStatus, Users user, Pageable pageable);

    @Query(value = "SELECT p FROM Posts p WHERE p.isActive = 1 and p.moderationStatus = 'ACCEPTED'  and  year(p.time) = year(:year) and p.time <= current_date", nativeQuery = true)
    Set<Posts> getYears(Date year);

    @Query(value = "SELECT count(*) from Posts where user_id = ?1", nativeQuery = true)
    Integer countByAuthor(Integer authorId);

    @Query(value = "SELECT * FROM Posts WHERE user_id = ?1 ORDER BY time ASC LIMIT 1", nativeQuery = true)
    Posts findFirstByTimeAndAuthor(Integer userId);

    @Query(value = "SELECT sum(p.view_count) FROM Posts p", nativeQuery = true)
    Integer countByViewCount();

    @Query(value = "SELECT * FROM Posts p ORDER BY p.time LIMIT 1", nativeQuery = true)
    Posts findFirstByTime();

    @Query(value = "SELECT * FROM Posts where user_id = ?1", nativeQuery = true)
    Optional<List<Posts>> findByAuthor(Integer userId);

    @Query(value = "SELECT SUM(p.view_count) FROM Posts p WHERE p.user_id = ?1 AND p.moderation_status = 'ACCEPTED' AND p.time <= current_date", nativeQuery = true)
    Integer countViews(Integer authorId);

    @Query(value = "SELECT COUNT(*) FROM Posts WHERE moderation_status = 'ACCEPTED' AND time <= current_date", nativeQuery = true)
    Integer countPosts();

}
