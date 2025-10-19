package ru.tihomirov.university.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "students")
@Data
@Accessors(chain = true)
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_id_seq")
    @SequenceGenerator(name = "student_id_seq", sequenceName = "student_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    private String lastName;
    private String name;
    private String middleName;
    private String email;
    private String phone;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;
}
