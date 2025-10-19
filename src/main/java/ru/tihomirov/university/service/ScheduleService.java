package ru.tihomirov.university.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tihomirov.university.dto.ScheduleInfoDto;
import ru.tihomirov.university.model.Schedule;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    Schedule save(Schedule schedule);
    Schedule update(Long id, Schedule updatedSchedule);
    void delete(Long id);
    Schedule getById(Long id);
    List<Schedule> getAll();

    List<Schedule> getByGroupId(Long groupId);
    Page<Schedule> getByGroupIdPaged(Long groupId, Pageable pageable);

    List<Schedule> getByTeacherId(Long teacherId);
    Page<Schedule> getByTeacherIdPaged(Long teacherId, Pageable pageable);

    List<Schedule> getByDate(LocalDate date);

    List<ScheduleInfoDto> getFormattedScheduleByGroup(Long groupId);
    List<ScheduleInfoDto> getFormattedScheduleByTeacher(Long teacherId);

    List<ScheduleInfoDto> getFormattedScheduleByGroupName(String groupName);
    List<ScheduleInfoDto> getFormattedScheduleByTeacherEmail(String email);

}
