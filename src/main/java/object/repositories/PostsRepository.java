package object.repositories;

import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.model.Posts;
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

@Repository
public interface PostsRepository extends CrudRepository<Posts,Integer> {


    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
    List<Posts> findAll(Pageable pageable);

    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date and text LIKE CONCAT('%', :query, '%')")
    Optional<List<Posts>> findBySearch(String query, Pageable pageable);

    @Query(value = "FROM Posts WHERE year(time) = :year and month(time) = :month and day(time) = :day")
    List<Posts> findByDate(Integer year, Integer month, Integer day, Pageable pageable);

//    @Query(value = "select count(p) from Posts p WHERE isActive = 1 and moderationStatus = 'ACCEPTED' and time <= current_date")
//    Integer getCount();

//    @Query(value = "FROM Posts p " +
//            "JOIN Post_Comments pc ON pc.post_id = p.id " +
//            "WHERE p.is_active = :active and " +
//            "p.moderation_status = :status and " +
//            "p.time < :date " +
//            "GROUP BY p.id " +
//            "ORDER BY count(pc.id)")
//    List<Posts> findByPopular(Integer active, ModerationStatus status, Date date, Pageable pageable);
//
//    @Query(value = "SELECT * FROM Posts p" +
//            "JOIN Posts_Votes pv ON pv.post_id == p.id " +
//            "WHERE p.is_active = :active and" +
//            "p.moderation_status = :status and" +
//            "p.time < :date" +
//            "GROUP BY p.id" +
//            "ORDER BY count(pv.Value) DESC",
//            nativeQuery = true )
//    List<Posts> findByBest(@Param("active") int isActive, @Param("status") ModerationStatus status, Date date, Pageable pageable);
//
//    @Query(value = "SELECT * FROM Posts " +
//            "WHERE is_active = :active and" +
//            "time < :date" +
//            " moderation_status = :status " +
//            "ORDER BY time",
//            nativeQuery = true )
//    List<Posts> findByEarly(@Param("active") int isActive, @Param("status") ModerationStatus status, Date date, Pageable pageable);

    @Query(value = "SELECT * from posts", nativeQuery = true)
    List<Posts> getAll();



    List<Posts> findAllByIsActiveAndModerationStatusAndTimeBefore(Integer active, ModerationStatus status, Date date, Pageable pageable); // здравствуй ultimate

    Optional<List<Posts>> findAllByIsActiveAndModerationStatusAndTimeBeforeAndTitleContaining (Integer active, ModerationStatus status, Date date, String query, Pageable pageable);

    Optional<Posts> findByTitle(String query);

    List<Posts> findAllByIsActiveAndModerationStatusAndTime(Integer active, ModerationStatus status, Date time, Pageable pageable);

    Optional<List<Posts>> findAllByIsActiveAndModerationStatusAndTimeBeforeAndTagListContaining(Integer active, ModerationStatus status, Date date, String tag, Pageable pageable);


    Optional<List<Posts>> findAllByIsActiveAndModerationStatus(Integer active, ModerationStatus status, PageRequest time);



}
