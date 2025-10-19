package ru.tihomirov.university.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "teachers")
@Data
@Accessors(chain = true)
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teacher_id_seq")
    @SequenceGenerator(name = "teacher_id_seq", sequenceName = "teacher_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    private String lastName;
    private String name;
    private String middleName;
    private String email;
    private String phone;
}
