package ru.tihomirov.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.AttendanceStatus;

import java.util.Optional;

public interface AttendanceStatusRepository extends JpaRepository<AttendanceStatus, Long> {
    Optional<AttendanceStatus> findByAttendanceStatusName(String attendanceStatusName);
}
