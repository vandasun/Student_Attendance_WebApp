package ru.tihomirov.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.ClassType;

public interface ClassTypeRepository extends JpaRepository<ClassType, Long> {
}
