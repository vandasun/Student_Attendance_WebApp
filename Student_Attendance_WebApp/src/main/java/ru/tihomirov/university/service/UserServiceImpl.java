package ru.tihomirov.university.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.tihomirov.university.dto.RegisterRequest;
import ru.tihomirov.university.dto.RegisterResponse;
import ru.tihomirov.university.dto.UpdateUserRequest;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.*;
import ru.tihomirov.university.repository.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public RegisterResponse registerUser(RegisterRequest request) {
        Role role = roleRepository.findByName(request.getRole().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException("Role not found: " + request.getRole()));

        User user = new User()
                .setUsername(request.getUsername())
                .setPassword(passwordEncoder.encode(request.getPassword()))
                .setRole(role);

        if ("STUDENT".equalsIgnoreCase(request.getRole())) {
            Student student = studentRepository.findById(request.getRelatedId())
                    .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + request.getRelatedId()));
            user.setStudent(student);
        } else if ("TEACHER".equalsIgnoreCase(request.getRole())) {
            Teacher teacher = teacherRepository.findById(request.getRelatedId())
                    .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + request.getRelatedId()));
            user.setTeacher(teacher);
        }

        userRepository.save(user);
        return new RegisterResponse("User registered successfully", user.getUsername());
    }

    @Override
    public User updateUser(Long userId, UpdateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (request.getUsername() != null) {
            user.setUsername(request.getUsername());
        }

        if (request.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRole() != null) {
            Role role = roleRepository.findByName(request.getRole().toUpperCase())
                    .orElseThrow(() -> new EntityNotFoundException("Role not found: " + request.getRole()));
            user.setRole(role);

            user.setStudent(null);
            user.setTeacher(null);

            if ("STUDENT".equalsIgnoreCase(request.getRole())) {
                Student student = studentRepository.findById(request.getRelatedId())
                        .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + request.getRelatedId()));
                user.setStudent(student);
            } else if ("TEACHER".equalsIgnoreCase(request.getRole())) {
                Teacher teacher = teacherRepository.findById(request.getRelatedId())
                        .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + request.getRelatedId()));
                user.setTeacher(teacher);
            }
        }

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        Long studentId = user.getStudent() != null ? user.getStudent().getId() : null;
        Long teacherId = user.getTeacher() != null ? user.getTeacher().getId() : null;

        userRepository.deleteById(userId);

        if (studentId != null) {
            studentRepository.deleteById(studentId);
        }

        if (teacherId != null) {
            teacherRepository.deleteById(teacherId);
        }
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
