package object.repositories;

import object.model.enums.Mode;
import object.model.enums.ModerationStatus;
import object.model.Posts;
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

    Integer countByModerationStatus(ModerationStatus status);

//    @Query(value = "SELECT * FROM Posts " +
//            "WHERE is_active = :active and " +
//            "moderation_status = :status and" +
//            "time < :date" +
//            "ORDER BY time DESC",
//            nativeQuery = true )
//    List<Posts> findByRecent(@Param(value = "active") int isActive, @Param("status") ModerationStatus moderationStatus, Date date, Pageable pageable);
//
//    @Query(value = "SELECT * FROM Posts p " +
//            "JOIN Post_Comments pc ON pc.post_id = p.id " +
//            "WHERE p.is_active = :active and " +
//            "p.moderation_status = :status and" +
//            "p.time < :date" +
//            "GROUP BY p.id " +
//            "ORDER BY count(pc.id)",
//            nativeQuery = true )
//    List<Posts> findByPopular(@Param("active") int isActive, @Param("status") ModerationStatus status, Date date, Pageable pageable);
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


    List<Posts> findAllByIsActiveAndModerationStatusAndTimeBefore(Integer active, ModerationStatus status, Date date, Pageable pageable); // здравствуй ultimate

    Optional<List<Posts>> findAllByIsActiveAndModerationStatusAndTimeBeforeAndTitleContaining (Integer active, ModerationStatus status, Date date, String query, Pageable pageable);

    Optional<Posts> findByTitle(String query);

    List<Posts> findAllByIsActiveAndModerationStatusAndTime(Integer active, ModerationStatus status, Date time, Pageable pageable);

    Optional<List<Posts>> findAllByIsActiveAndModerationStatusAndTimeBeforeAndTagListContaining(Integer active, ModerationStatus status, Date date, String tag, Pageable pageable);


    Optional<List<Posts>> findAllByIsActiveAndModerationStatus(Integer active, ModerationStatus status, PageRequest time);
}
