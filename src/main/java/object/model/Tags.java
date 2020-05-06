package object.model;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
public class Tags {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(nullable = false)
    private String name;

}
