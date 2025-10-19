package ru.tihomirov.university.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.Attendance;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long>{
    List<Attendance> findByScheduleId(Long scheduleId);
    Page<Attendance> findByScheduleId(Long scheduleId, Pageable pageable);

    List<Attendance> findByStudentId(Long studentId);
    Page<Attendance> findByStudentId(Long studentId, Pageable pageable);
}
