package ru.tihomirov.university.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.tihomirov.university.dto.AttendanceInfoDto;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Attendance;
import ru.tihomirov.university.repository.ScheduleRepository;
import ru.tihomirov.university.repository.StudentRepository;
import ru.tihomirov.university.repository.TeacherRepository;
import ru.tihomirov.university.security.UserDetailsImpl;
import ru.tihomirov.university.service.AttendanceService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;
    private final ScheduleRepository scheduleRepository;
    private final StudentRepository studentRepository;

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<Attendance> create(@RequestBody Attendance attendance) {
        UserDetailsImpl userDetails = getCurrentUser();

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может создавать записи о посещаемости");
        }

        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = scheduleRepository.findById(attendance.getSchedule().getId())
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found"))
                    .getTeacher().getId();

            if (!scheduleTeacherId.equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может отмечать посещаемость только на своих занятиях");
            }
        }

        return ResponseEntity.ok(attendanceService.save(attendance));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Attendance> update(@PathVariable Long id, @RequestBody Attendance attendance) {
        UserDetailsImpl userDetails = getCurrentUser();

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может редактировать записи о посещаемости");
        }

        Attendance existing = attendanceService.getById(id);

        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = existing.getSchedule().getTeacher().getId();
            if (!scheduleTeacherId.equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может редактировать только посещаемость на своих занятиях");
            }
        }

        return ResponseEntity.ok(attendanceService.update(id, attendance));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserDetailsImpl userDetails = getCurrentUser();
        Attendance existing = attendanceService.getById(id);

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может удалять записи о посещаемости");
        }

        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = existing.getSchedule().getTeacher().getId();
            if (!scheduleTeacherId.equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может удалять только посещаемость на своих занятиях");
            }
        }

        attendanceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Attendance>> getAll() {
        UserDetailsImpl userDetails = getCurrentUser();

        if (userDetails.hasRole("STUDENT")) {
            return ResponseEntity.ok(attendanceService.getByStudentId(userDetails.getStudentId()));
        }

        return ResponseEntity.ok(attendanceService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Attendance> getById(@PathVariable Long id) {
        UserDetailsImpl userDetails = getCurrentUser();
        Attendance attendance = attendanceService.getById(id);

        if (userDetails.hasRole("STUDENT")) {
            if (!attendance.getStudent().getId().equals(userDetails.getStudentId())) {
                throw new AccessDeniedException("Студент может просматривать только свою посещаемость");
            }
        }

        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = attendance.getSchedule().getTeacher().getId();
            if (!scheduleTeacherId.equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может просматривать только посещаемость на своих занятиях");
            }
        }

        return ResponseEntity.ok(attendance);
    }

    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<List<Attendance>> getBySchedule(@PathVariable Long scheduleId) {
        UserDetailsImpl userDetails = getCurrentUser();

        scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new EntityNotFoundException("Расписание с ID " + scheduleId + " не найдена")
        );

        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found"))
                    .getTeacher().getId();

            if (!scheduleTeacherId.equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может просматривать посещаемость только на своих занятиях");
            }
        }

        return ResponseEntity.ok(attendanceService.getByScheduleId(scheduleId));
    }

    @GetMapping("/schedule/{scheduleId}/page")
    public ResponseEntity<Page<Attendance>> getBySchedulePaged(@PathVariable Long scheduleId, Pageable pageable) {
        UserDetailsImpl userDetails = getCurrentUser();

        scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new EntityNotFoundException("Расписание с ID " + scheduleId + " не найдена")
        );

        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found"))
                    .getTeacher().getId();

            if (!scheduleTeacherId.equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может просматривать посещаемость только на своих занятиях");
            }
        }

        return ResponseEntity.ok(attendanceService.getByScheduleIdPaged(scheduleId, pageable));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Attendance>> getByStudent(@PathVariable Long studentId) {
        UserDetailsImpl userDetails = getCurrentUser();

        studentRepository.findById(studentId).orElseThrow(() ->
                new EntityNotFoundException("Студент с ID " + studentId + " не найден")
        );

        if (userDetails.hasRole("STUDENT") && !userDetails.getStudentId().equals(studentId) || userDetails.hasRole("TEACHER")) {
            throw new AccessDeniedException("Студент может просматривать только свою посещаемость");
        }

        return ResponseEntity.ok(attendanceService.getByStudentId(studentId));
    }

    @GetMapping("/student/{studentId}/page")
    public ResponseEntity<Page<Attendance>> getByStudentPaged(@PathVariable Long studentId, Pageable pageable) {
        UserDetailsImpl userDetails = getCurrentUser();

        studentRepository.findById(studentId).orElseThrow(() ->
                new EntityNotFoundException("Студент с ID " + studentId + " не найден")
        );

        if (userDetails.hasRole("STUDENT") && !userDetails.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Студент может просматривать только свою посещаемость");
        }

        return ResponseEntity.ok(attendanceService.getByStudentIdPaged(studentId, pageable));
    }

    @GetMapping("/status/{statusId}")
    public ResponseEntity<List<Attendance>> getByStatus(@PathVariable Long statusId) {
        UserDetailsImpl userDetails = getCurrentUser();

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может фильтровать по статусам посещаемости");
        }

        return ResponseEntity.ok(attendanceService.getByAttendanceStatusId(statusId));
    }

    @GetMapping("/schedule/{scheduleId}/info")
    public ResponseEntity<List<AttendanceInfoDto>> getFormattedBySchedule(@PathVariable Long scheduleId) {
        UserDetailsImpl userDetails = getCurrentUser();

        scheduleRepository.findById(scheduleId).orElseThrow(() ->
                new EntityNotFoundException("Расписание с ID " + scheduleId + " не найдена")
        );

        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found"))
                    .getTeacher().getId();

            if (!scheduleTeacherId.equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может просматривать посещаемость только на своих занятиях");
            }
        }

        List<AttendanceInfoDto> result = attendanceService.getFormattedAttendanceBySchedule(scheduleId);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Для расписания с ID " + scheduleId + " не найдено записей о посещаемости");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/student/{studentId}/info")
    public ResponseEntity<List<AttendanceInfoDto>> getFormattedByStudent(@PathVariable Long studentId) {
        UserDetailsImpl userDetails = getCurrentUser();

        studentRepository.findById(studentId).orElseThrow(() ->
                new EntityNotFoundException("Студент с ID " + studentId + " не найден")
        );

        if (userDetails.hasRole("STUDENT") && !userDetails.getStudentId().equals(studentId)) {
            throw new AccessDeniedException("Студент может просматривать только свою посещаемость");
        }

        List<AttendanceInfoDto> result = attendanceService.getFormattedAttendanceByStudent(studentId);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Для студента с ID " + studentId + " не найдено записей о посещаемости");
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/mark")
    public ResponseEntity<Attendance> markAttendance(
            @RequestParam Long scheduleId,
            @RequestParam Long studentId,
            @RequestParam Long statusId) {

        UserDetailsImpl userDetails = getCurrentUser();

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может отмечать посещаемость");
        }

        Long teacherId = userDetails.getTeacherId();
        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found"))
                    .getTeacher().getId();

            if (!scheduleTeacherId.equals(teacherId)) {
                throw new AccessDeniedException("Преподаватель может отмечать посещаемость только на своих занятиях");
            }
        }

        return ResponseEntity.ok(attendanceService.markAttendance(scheduleId, studentId, teacherId, statusId));
    }

    @PostMapping("/mark-group")
    public ResponseEntity<List<Attendance>> markAttendanceForGroup(
            @RequestParam Long scheduleId,
            @RequestParam List<Long> studentIds,
            @RequestParam Long statusId) {

        UserDetailsImpl userDetails = getCurrentUser();

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может отмечать посещаемость");
        }

        Long teacherId = userDetails.getTeacherId();
        if (userDetails.hasRole("TEACHER")) {
            Long scheduleTeacherId = scheduleRepository.findById(scheduleId)
                    .orElseThrow(() -> new EntityNotFoundException("Schedule not found"))
                    .getTeacher().getId();

            if (!scheduleTeacherId.equals(teacherId)) {
                throw new AccessDeniedException("Преподаватель может отмечать посещаемость только на своих занятиях");
            }
        }

        return ResponseEntity.ok(attendanceService.markAttendanceForGroup(scheduleId, teacherId, studentIds, statusId));
    }
}