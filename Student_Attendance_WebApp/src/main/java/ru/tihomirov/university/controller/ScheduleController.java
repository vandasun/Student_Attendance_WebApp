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
import ru.tihomirov.university.dto.ScheduleInfoDto;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Schedule;
import ru.tihomirov.university.repository.GroupRepository;
import ru.tihomirov.university.repository.TeacherRepository;
import ru.tihomirov.university.security.UserDetailsImpl;
import ru.tihomirov.university.service.ScheduleService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;

    private UserDetailsImpl getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (UserDetailsImpl) authentication.getPrincipal();
    }

    @PostMapping
    public ResponseEntity<Schedule> create(@RequestBody Schedule schedule) {
        UserDetailsImpl userDetails = getCurrentUser();

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может создавать расписание");
        }

        if (userDetails.hasRole("TEACHER")) {
            if (!schedule.getTeacher().getId().equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может создавать только своё расписание");
            }
        }

        return ResponseEntity.ok(scheduleService.save(schedule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> update(@PathVariable Long id, @RequestBody Schedule schedule) {
        UserDetailsImpl userDetails = getCurrentUser();

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может редактировать расписание");
        }

        if (userDetails.hasRole("TEACHER")) {
            if (!schedule.getTeacher().getId().equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может редактировать только своё расписание");
            }
        }

        return ResponseEntity.ok(scheduleService.update(id, schedule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        UserDetailsImpl userDetails = getCurrentUser();
        Schedule existing = scheduleService.getById(id);

        if (userDetails.hasRole("STUDENT")) {
            throw new AccessDeniedException("Студент не может удалять расписание");
        }

        if (userDetails.hasRole("TEACHER")) {
            if (!existing.getTeacher().getId().equals(userDetails.getTeacherId())) {
                throw new AccessDeniedException("Преподаватель может удалять только своё расписание");
            }
        }

        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Schedule>> getAll() {
        return ResponseEntity.ok(scheduleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getById(@PathVariable Long id) {
        return ResponseEntity.ok(scheduleService.getById(id));
    }

    @GetMapping("/group/{groupId}")
    public ResponseEntity<List<Schedule>> getByGroup(@PathVariable Long groupId) {
        groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Группа с ID " + groupId + " не найдена")
        );
        return ResponseEntity.ok(scheduleService.getByGroupId(groupId));
    }

    @GetMapping("/group/{groupId}/page")
    public ResponseEntity<Page<Schedule>> getByGroupPaged(@PathVariable Long groupId, Pageable pageable) {
        groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Группа с ID " + groupId + " не найдена")
        );
        return ResponseEntity.ok(scheduleService.getByGroupIdPaged(groupId, pageable));
    }

    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<List<Schedule>> getByTeacher(@PathVariable Long teacherId) {
        teacherRepository.findById(teacherId).orElseThrow(() ->
                new EntityNotFoundException("Преподаватель с ID " + teacherId + " не найден")
        );
        return ResponseEntity.ok(scheduleService.getByTeacherId(teacherId));
    }

    @GetMapping("/teacher/{teacherId}/page")
    public ResponseEntity<Page<Schedule>> getByTeacherPaged(@PathVariable Long teacherId, Pageable pageable) {
        teacherRepository.findById(teacherId).orElseThrow(() ->
                new EntityNotFoundException("Преподаватель с ID " + teacherId + " не найден")
        );
        return ResponseEntity.ok(scheduleService.getByTeacherIdPaged(teacherId, pageable));
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Schedule>> getByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(scheduleService.getByDate(date));
    }

    @GetMapping("/group/{groupId}/info")
    public ResponseEntity<List<ScheduleInfoDto>> getFormattedByGroup(@PathVariable Long groupId) {
        groupRepository.findById(groupId).orElseThrow(() ->
                new EntityNotFoundException("Группа с ID " + groupId + " не найдена")
        );
        return ResponseEntity.ok(scheduleService.getFormattedScheduleByGroup(groupId));
    }

    @GetMapping("/teacher/{teacherId}/info")
    public ResponseEntity<List<ScheduleInfoDto>> getFormattedByTeacher(@PathVariable Long teacherId) {
        teacherRepository.findById(teacherId).orElseThrow(() ->
                new EntityNotFoundException("Преподаватель с ID " + teacherId + " не найден")
        );
        return ResponseEntity.ok(scheduleService.getFormattedScheduleByTeacher(teacherId));
    }

    @GetMapping("/group/name/{groupName}/info")
    public ResponseEntity<List<ScheduleInfoDto>> getByGroupName(@PathVariable String groupName) {
        List<ScheduleInfoDto> result = scheduleService.getFormattedScheduleByGroupName(groupName);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Группа с названием '" + groupName + "' не найдена или у неё нет занятий");
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/teacher/email/{email}/info")
    public ResponseEntity<List<ScheduleInfoDto>> getByTeacherEmail(@PathVariable String email) {
        List<ScheduleInfoDto> result = scheduleService.getFormattedScheduleByTeacherEmail(email);
        if (result.isEmpty()) {
            throw new EntityNotFoundException("Преподаватель с email '" + email + "' не найден или у него нет занятий");
        }
        return ResponseEntity.ok(result);
    }
}
