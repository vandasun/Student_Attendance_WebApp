package ru.tihomirov.university.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "groups")
@Data
@Accessors(chain = true)
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "group_id_seq")
    @SequenceGenerator(name = "group_id_seq", sequenceName = "group_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    private String name;
    private Integer yearCreated;
    private Integer countStudents;
    private Integer maxCountStudents;
}
