package ru.tihomirov.university.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.Student;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByGroupId(Long groupId);
    Page<Student> findAllByGroupId(Long groupId, Pageable pageable);
}
