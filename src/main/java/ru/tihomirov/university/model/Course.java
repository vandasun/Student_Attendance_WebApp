package ru.tihomirov.university.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "courses")
@Data
@Accessors(chain = true)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "course_id_seq")
    @SequenceGenerator(name = "course_id_seq", sequenceName = "course_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    private String name;
    private Integer lectureCount;
    private Integer seminarCount;
    private Integer labCount;
}
