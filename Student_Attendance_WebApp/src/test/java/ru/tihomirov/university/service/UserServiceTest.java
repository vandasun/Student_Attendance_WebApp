package ru.tihomirov.university.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.tihomirov.university.dto.RegisterRequest;
import ru.tihomirov.university.dto.RegisterResponse;
import ru.tihomirov.university.dto.UpdateUserRequest;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.*;
import ru.tihomirov.university.repository.*;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void shouldRegisterStudentSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("student123");
        request.setPassword("pass123");
        request.setRole("STUDENT");
        request.setRelatedId(1L);

        Role role = new Role().setId(1L).setName("STUDENT");
        Student student = new Student().setId(1L);

        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(passwordEncoder.encode("pass123")).thenReturn("encoded_pass");

        RegisterResponse response = userService.registerUser(request);

        assertEquals("User registered successfully", response.getMessage());
        assertEquals("student123", response.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldRegisterTeacherSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("teacher123");
        request.setPassword("pass456");
        request.setRole("TEACHER");
        request.setRelatedId(2L);

        Role role = new Role().setId(2L).setName("TEACHER");
        Teacher teacher = new Teacher().setId(2L);

        when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(role));
        when(teacherRepository.findById(2L)).thenReturn(Optional.of(teacher));
        when(passwordEncoder.encode("pass456")).thenReturn("encoded456");

        RegisterResponse response = userService.registerUser(request);

        assertEquals("User registered successfully", response.getMessage());
        assertEquals("teacher123", response.getUsername());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowWhenRoleNotFound() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("user1");
        request.setPassword("qwerty");
        request.setRole("UNKNOWN");

        when(roleRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.registerUser(request));

        assertEquals("Role not found: UNKNOWN", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenStudentNotFound() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("s_user");
        request.setPassword("pass");
        request.setRole("STUDENT");
        request.setRelatedId(99L);

        Role role = new Role().setId(1L).setName("STUDENT");

        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));
        when(studentRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.registerUser(request));

        assertEquals("Student not found with id: 99", ex.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldThrowWhenTeacherNotFound() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("t_user");
        request.setPassword("pass");
        request.setRole("TEACHER");
        request.setRelatedId(77L);

        Role role = new Role().setId(2L).setName("TEACHER");

        when(roleRepository.findByName("TEACHER")).thenReturn(Optional.of(role));
        when(teacherRepository.findById(77L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.registerUser(request));

        assertEquals("Teacher not found with id: 77", ex.getMessage());
        verify(userRepository, never()).save(any());
    }


    @Test
    void shouldUpdateUserSuccessfully() {
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("updatedUser");
        request.setPassword("newpass");
        request.setRole("STUDENT");
        request.setRelatedId(100L);

        User existingUser = new User().setId(userId).setUsername("oldUser").setPassword("oldpass").setRole(new Role().setName("OLD"));
        Role newRole = new Role().setName("STUDENT");
        Student student = new Student().setId(100L);

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(newRole));
        when(studentRepository.findById(100L)).thenReturn(Optional.of(student));
        when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");

        // ВАЖНО: мокаем сохранение
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        User updatedUser = userService.updateUser(userId, request);

        assertEquals("updatedUser", updatedUser.getUsername());
        assertEquals("encodedNewPass", updatedUser.getPassword());
        assertEquals(newRole, updatedUser.getRole());
        assertEquals(student, updatedUser.getStudent());
        verify(userRepository).save(existingUser);
    }


    @Test
    void shouldThrowWhenUpdateRoleNotFound() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRole("UNKNOWN");

        User user = new User().setId(2L);
        when(userRepository.findById(2L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("UNKNOWN")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(2L, request));

        assertEquals("Role not found: UNKNOWN", ex.getMessage());
    }

    @Test
    void shouldThrowWhenUpdateStudentNotFound() {
        UpdateUserRequest request = new UpdateUserRequest();
        request.setRole("STUDENT");
        request.setRelatedId(999L);

        User user = new User().setId(3L);
        Role role = new Role().setId(1L).setName("STUDENT");

        when(userRepository.findById(3L)).thenReturn(Optional.of(user));
        when(roleRepository.findByName("STUDENT")).thenReturn(Optional.of(role));
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser(3L, request));

        assertEquals("Student not found with id: 999", ex.getMessage());
    }


    @Test
    void shouldDeleteUserSuccessfully() {
        Student student = new Student().setId(10L);
        User user = new User().setId(5L).setStudent(student);

        when(userRepository.findById(5L)).thenReturn(Optional.of(user));

        userService.deleteUser(5L);

        verify(userRepository).deleteById(5L);
        verify(studentRepository).deleteById(10L);
    }

    @Test
    void shouldThrowWhenDeleteUserNotFound() {
        when(userRepository.findById(404L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser(404L));

        assertEquals("User not found with id: 404", ex.getMessage());
    }
}
