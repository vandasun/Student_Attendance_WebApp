package ru.tihomirov.university.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.tihomirov.university.dto.AttendanceInfoDto;
import ru.tihomirov.university.model.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceService {
    Attendance save(Attendance attendance);
    Attendance update(Long id, Attendance updatedAttendance);
    void delete(Long id);
    Attendance getById(Long id);
    List<Attendance> getAll();

    List<Attendance> getByScheduleId(Long scheduleId);
    Page<Attendance> getByScheduleIdPaged(Long scheduleId, Pageable pageable);

    List<Attendance> getByStudentId(Long studentId);
    Page<Attendance> getByStudentIdPaged(Long studentId, Pageable pageable);

    List<Attendance> getByAttendanceStatusId(Long statusId);

    List<AttendanceInfoDto> getFormattedAttendanceBySchedule(Long scheduleId);
    List<AttendanceInfoDto> getFormattedAttendanceByStudent(Long studentId);

    Attendance markAttendance(Long scheduleId, Long studentId, Long teacherId, Long statusId);
    List<Attendance> markAttendanceForGroup(Long scheduleId, Long teacherId, List<Long> studentIds, Long statusId);
}