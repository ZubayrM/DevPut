package object.model;


import lombok.Data;
import object.model.enums.ModerationStatus;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "is_active", nullable = false)
    private Integer isActive;

    @Enumerated(EnumType.STRING)
    @Column(name = "moderation_status", nullable = false)
    private ModerationStatus moderationStatus;

    @Column(name = "moderation_id")
    private Integer moderationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Users author;

    @Column(nullable = false)
    private Date time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount;


    @OneToMany
    @JoinTable(name = "post_votes",
            joinColumns = {@JoinColumn(name = "post_id", referencedColumnName = "id")})
    private List<PostVotes> postVotesList;

    @OneToMany
    @JoinTable(name = "post_comments",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "id")
    )
    private List<PostComments> postCommentsList;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "tag2post",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "id"))
    Set<Tags> tagList = new HashSet<>();

}
