package ru.tihomirov.university.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Teacher;
import ru.tihomirov.university.repository.TeacherRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TeacherServiceTest {

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Teacher sampleTeacher() {
        return new Teacher()
                .setId(1L)
                .setLastName("Иванов")
                .setName("Иван")
                .setMiddleName("Иванович")
                .setEmail("ivanov@university.com")
                .setPhone("1234567890");
    }

    @Test
    void shouldReturnAllTeachers() {
        Teacher teacher = sampleTeacher();
        when(teacherRepository.findAll()).thenReturn(List.of(teacher));

        List<Teacher> result = teacherService.getAll();

        assertEquals(1, result.size());
        assertEquals("ivanov@university.com", result.get(0).getEmail());
    }

    @Test
    void shouldReturnTeacherById() {
        Teacher teacher = sampleTeacher();
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.getById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Иван", result.getName());
    }

    @Test
    void shouldThrowWhenTeacherByIdNotFound() {
        when(teacherRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                teacherService.getById(99L));

        assertEquals("Teacher not found with id: 99", ex.getMessage());
    }

    @Test
    void shouldReturnTeacherByEmail() {
        Teacher teacher = sampleTeacher();
        when(teacherRepository.findByEmail("ivanov@university.com")).thenReturn(Optional.of(teacher));

        Teacher result = teacherService.getByEmail("ivanov@university.com");

        assertEquals("Иванов", result.getLastName());
        assertEquals("Иван", result.getName());
    }

    @Test
    void shouldThrowWhenTeacherByEmailNotFound() {
        when(teacherRepository.findByEmail("missing@university.com")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                teacherService.getByEmail("missing@university.com"));

        assertEquals("Teacher not found with email: missing@university.com", ex.getMessage());
    }

    @Test
    void shouldSaveTeacherSuccessfully() {
        Teacher teacher = sampleTeacher();
        when(teacherRepository.save(teacher)).thenReturn(teacher);

        Teacher result = teacherService.save(teacher);

        assertEquals("Иван", result.getName());
        verify(teacherRepository, times(1)).save(teacher);
    }

    @Test
    void shouldDeleteTeacherById() {
        when(teacherRepository.existsById(1L)).thenReturn(true);

        teacherService.deleteById(1L);

        verify(teacherRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingMissingTeacher() {
        when(teacherRepository.existsById(77L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class, () ->
                teacherService.deleteById(77L));

        assertEquals("Teacher not found with id: 77", ex.getMessage());
    }
}
