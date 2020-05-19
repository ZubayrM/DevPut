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


    @Query(value = "FROM Posts WHERE isActive = 1 and moderationStatus = ?1", nativeQuery = true)
    Optional<List<Posts>> findByModerationStatus(String status, Pageable pageable);








    //////////////////////////////////////////////////////////////////////
    @Query(value = "SELECT * from posts", nativeQuery = true)
    List<Posts> getAll();



    List<Posts> findAllByIsActiveAndModerationStatusAndTimeBefore(Integer active, ModerationStatus status, Date date, Pageable pageable); // здравствуй ultimate

    Optional<List<Posts>> findAllByIsActiveAndModerationStatusAndTimeBeforeAndTitleContaining (Integer active, ModerationStatus status, Date date, String query, Pageable pageable);

    Optional<Posts> findByTitle(String query);

    List<Posts> findAllByIsActiveAndModerationStatusAndTime(Integer active, ModerationStatus status, Date time, Pageable pageable);

    Optional<List<Posts>> findAllByIsActiveAndModerationStatusAndTimeBeforeAndTagListContaining(Integer active, ModerationStatus status, Date date, String tag, Pageable pageable);


    Optional<List<Posts>> findAllByIsActiveAndModerationStatus(Integer active, ModerationStatus status, PageRequest time);



}
