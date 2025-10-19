package ru.tihomirov.university.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tihomirov.university.aop.LogExecutionTime;
import ru.tihomirov.university.dto.AttendanceInfoDto;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Attendance;
import ru.tihomirov.university.model.AttendanceStatus;
import ru.tihomirov.university.model.Schedule;
import ru.tihomirov.university.model.Student;
import ru.tihomirov.university.repository.AttendanceRepository;
import ru.tihomirov.university.repository.AttendanceStatusRepository;
import ru.tihomirov.university.repository.ScheduleRepository;
import ru.tihomirov.university.repository.StudentRepository;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;

    @Override
    @LogExecutionTime
    @Transactional
    public Attendance save(Attendance attendance) {
        Schedule schedule = scheduleRepository.findById(attendance.getSchedule().getId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        Student student = studentRepository.findById(attendance.getStudent().getId())
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        AttendanceStatus status = attendanceStatusRepository.findById(attendance.getAttendanceStatus().getId())
                .orElseThrow(() -> new EntityNotFoundException("AttendanceStatus not found"));

        if (attendanceRepository.findByScheduleId(schedule.getId()).stream()
                .anyMatch(a -> a.getStudent().getId().equals(student.getId()))) {
            throw new IllegalStateException("Attendance already marked for this student on this schedule");
        }

        attendance.setSchedule(schedule);
        attendance.setStudent(student);
        attendance.setAttendanceStatus(status);

        if (attendance.getMarkedTime() == null) {
            attendance.setMarkedTime(LocalTime.now());
        }

        return attendanceRepository.save(attendance);
    }

    @Override
    @LogExecutionTime
    @Transactional
    public Attendance update(Long id, Attendance updatedAttendance) {
        Attendance existing = attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));

        if (updatedAttendance.getSchedule() != null) {
            Schedule schedule = scheduleRepository.findById(updatedAttendance.getSchedule().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
            existing.setSchedule(schedule);
        }

        if (updatedAttendance.getStudent() != null) {
            Student student = studentRepository.findById(updatedAttendance.getStudent().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Student not found"));
            existing.setStudent(student);
        }

        if (updatedAttendance.getAttendanceStatus() != null) {
            AttendanceStatus status = attendanceStatusRepository.findById(updatedAttendance.getAttendanceStatus().getId())
                    .orElseThrow(() -> new EntityNotFoundException("AttendanceStatus not found"));
            existing.setAttendanceStatus(status);
        }

        if (updatedAttendance.getMarkedTime() != null) {
            existing.setMarkedTime(updatedAttendance.getMarkedTime());
        }

        return attendanceRepository.save(existing);
    }

    @Override
    @LogExecutionTime
    @Transactional
    public void delete(Long id) {
        if (!attendanceRepository.existsById(id)) {
            throw new EntityNotFoundException("Attendance not found with id: " + id);
        }
        attendanceRepository.deleteById(id);
    }

    @Override
    public Attendance getById(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Attendance not found with id: " + id));
    }

    @Override
    public List<Attendance> getAll() {
        return attendanceRepository.findAll();
    }

    @Override
    public List<Attendance> getByScheduleId(Long scheduleId) {
        return attendanceRepository.findByScheduleId(scheduleId);
    }

    @Override
    public Page<Attendance> getByScheduleIdPaged(Long scheduleId, Pageable pageable) {
        return attendanceRepository.findByScheduleId(scheduleId, pageable);
    }

    @Override
    public List<Attendance> getByStudentId(Long studentId) {
        return attendanceRepository.findByStudentId(studentId);
    }

    @Override
    public Page<Attendance> getByStudentIdPaged(Long studentId, Pageable pageable) {
        return attendanceRepository.findByStudentId(studentId, pageable);
    }

    @Override
    public List<Attendance> getByAttendanceStatusId(Long statusId) {
        return attendanceRepository.findAll().stream()
                .filter(attendance -> attendance.getAttendanceStatus().getId().equals(statusId))
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceInfoDto> getFormattedAttendanceBySchedule(Long scheduleId) {
        return attendanceRepository.findByScheduleId(scheduleId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttendanceInfoDto> getFormattedAttendanceByStudent(Long studentId) {
        return getByStudentId(studentId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @LogExecutionTime
    @Transactional
    public Attendance markAttendance(Long scheduleId, Long studentId, Long teacherId, Long statusId) {
        Attendance attendance = new Attendance();

        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EntityNotFoundException("Student not found"));
        AttendanceStatus status = attendanceStatusRepository.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("AttendanceStatus not found"));

        attendance.setSchedule(schedule)
                .setStudent(student)
                .setAttendanceStatus(status)
                .setMarkedTime(LocalTime.now());

        return save(attendance);
    }

    @Override
    @LogExecutionTime
    @Transactional
    public List<Attendance> markAttendanceForGroup(Long scheduleId, Long teacherId, List<Long> studentIds, Long statusId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found"));
        AttendanceStatus status = attendanceStatusRepository.findById(statusId)
                .orElseThrow(() -> new EntityNotFoundException("AttendanceStatus not found"));

        return studentIds.stream()
                .map(studentId -> {
                    Student student = studentRepository.findById(studentId)
                            .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + studentId));

                    Attendance attendance = new Attendance();
                    attendance.setSchedule(schedule)
                            .setStudent(student)
                            .setAttendanceStatus(status)
                            .setMarkedTime(LocalTime.now());

                    return attendanceRepository.save(attendance);
                })
                .collect(Collectors.toList());
    }

    private AttendanceInfoDto mapToDto(Attendance attendance) {
        return new AttendanceInfoDto()
                .setStudentFullName(attendance.getStudent().getLastName() + " " +
                        attendance.getStudent().getName() + " " +
                        attendance.getStudent().getMiddleName())
                .setGroupName(attendance.getSchedule().getGroup().getName())
                .setTeacherFullName(attendance.getSchedule().getTeacher().getLastName() + " " +
                        attendance.getSchedule().getTeacher().getName() + " " +
                        attendance.getSchedule().getTeacher().getMiddleName())
                .setAttendanceStatusName(attendance.getAttendanceStatus().getAttendanceStatusName())
                .setMarkedTime(attendance.getMarkedTime())
                .setScheduleStartTime(attendance.getSchedule().getStartTime())
                .setScheduleEndTime(attendance.getSchedule().getEndTime())
                .setCourseName(attendance.getSchedule().getCourse().getName());
    }
}