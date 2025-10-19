package ru.tihomirov.university.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tihomirov.university.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
