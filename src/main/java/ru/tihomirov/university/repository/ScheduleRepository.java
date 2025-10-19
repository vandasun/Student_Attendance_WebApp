package ru.tihomirov.university.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByGroupId(Long groupId);
    Page<Schedule> findByGroupId(Long groupId, Pageable pageable);

    List<Schedule> findByTeacherId(Long teacherId);
    Page<Schedule> findByTeacherId(Long teacherId, Pageable pageable);

    List<Schedule> findByDate(LocalDate date);
    Page<Schedule> findAll(Pageable pageable);

    boolean existsByGroupIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Long groupId, LocalDate date, LocalTime endTime, LocalTime startTime
    );

    boolean existsByTeacherIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(
            Long teacherId, LocalDate date, LocalTime endTime, LocalTime startTime
    );

    List<Schedule> findByGroup_Name(String name);
    List<Schedule> findByTeacher_Email(String email);
}
