package ru.tihomirov.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.Teacher;

import java.util.Optional;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
    Optional<Teacher> findByEmail(String email);
}
