package object.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Tag2Post {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "post_id", nullable = false)
    private Integer postId;

    @Column(name = "tag_id", nullable = false)
    private Integer tagId;

}
