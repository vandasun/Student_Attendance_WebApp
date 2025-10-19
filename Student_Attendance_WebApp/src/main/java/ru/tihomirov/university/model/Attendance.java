package ru.tihomirov.university.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Entity
@Table(name = "attendance")
@Data
@Accessors(chain = true)
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_id_seq")
    @SequenceGenerator(name = "attendance_id_seq", sequenceName = "attendance_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private AttendanceStatus attendanceStatus;

    private LocalTime markedTime;
}
