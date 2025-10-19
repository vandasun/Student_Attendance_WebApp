package ru.tihomirov.university.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.experimental.Accessors;

@Entity
@Table(name = "attendance_status")
@Data
@Accessors(chain = true)
public class AttendanceStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attendance_status_id_seq")
    @SequenceGenerator(name = "attendance_status_id_seq", sequenceName = "attendance_status_id_seq", initialValue = 1, allocationSize = 1)
    private Long id;

    private String attendanceStatusName;
}
