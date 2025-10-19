package ru.tihomirov.university.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "schedule")
@Data
@Accessors(chain = true)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "schedule_id_seq")
    @SequenceGenerator(name = "schedule_id_seq", sequenceName = "schedule_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private Group group;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @ManyToOne
    @JoinColumn(name = "class_type_id")
    private ClassType classType;
}
