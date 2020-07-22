package object.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "post_votes")
@ToString//(exclude = "post")
@EqualsAndHashCode//(exclude = "post")
public class PostVotes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    @JsonManagedReference
    private Posts post;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private Integer value;



}
