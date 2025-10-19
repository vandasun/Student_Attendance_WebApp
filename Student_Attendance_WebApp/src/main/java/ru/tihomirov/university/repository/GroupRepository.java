package ru.tihomirov.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.Group;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, Long> {
    Optional<Group> findByName(String name);
}
