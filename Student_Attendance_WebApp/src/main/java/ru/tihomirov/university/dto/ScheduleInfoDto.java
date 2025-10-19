package ru.tihomirov.university.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Accessors(chain = true)
public class ScheduleInfoDto {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String teacherFullName;
    private String courseName;
    private String classTypeName;
    private String groupName;
}
