package ru.tihomirov.university.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalTime;

@Data
@Accessors(chain = true)
public class AttendanceInfoDto {
    private String studentFullName;
    private String groupName;
    private String teacherFullName;
    private String attendanceStatusName;
    private LocalTime markedTime;
    private LocalTime scheduleStartTime;
    private LocalTime scheduleEndTime;
    private String courseName;
}