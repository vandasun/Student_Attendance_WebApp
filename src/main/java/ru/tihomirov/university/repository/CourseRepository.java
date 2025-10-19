package ru.tihomirov.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.Course;

import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    Optional<Course> findByName(String name);
}
