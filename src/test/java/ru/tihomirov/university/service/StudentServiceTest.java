package ru.tihomirov.university.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.model.Student;
import ru.tihomirov.university.repository.GroupRepository;
import ru.tihomirov.university.repository.StudentRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class StudentServiceTest {

    @Mock private StudentRepository studentRepository;
    @Mock private GroupRepository groupRepository;

    @InjectMocks private StudentServiceImpl studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Group sampleGroup(Long id, int count, int max) {
        return new Group()
                .setId(id)
                .setName("ЦПИ-21")
                .setCountStudents(count)
                .setMaxCountStudents(max);
    }

    private Student sampleStudent(Long id, Group group) {
        return new Student()
                .setId(id)
                .setName("Иван")
                .setLastName("Иванов")
                .setMiddleName("Иванович")
                .setEmail("ivan@student.com")
                .setPhone("123456789")
                .setGroup(group);
    }

    @Test
    void shouldReturnAllStudents() {
        when(studentRepository.findAll()).thenReturn(List.of(new Student(), new Student()));
        List<Student> result = studentService.getAll();
        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnStudentById() {
        Student student = new Student().setId(1L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Student result = studentService.getById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenStudentNotFoundById() {
        when(studentRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> studentService.getById(100L));
    }

    @Test
    void shouldSaveStudentSuccessfully() {
        Group group = sampleGroup(1L, 5, 10);
        Student student = sampleStudent(null, group);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(studentRepository.save(any())).thenReturn(student);

        Student result = studentService.save(student);

        assertEquals(group, result.getGroup());
        verify(groupRepository).save(group);
    }

    @Test
    void shouldThrowWhenSavingToFullGroup() {
        Group group = sampleGroup(1L, 10, 10);
        Student student = sampleStudent(null, group);

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));

        assertThrows(IllegalStateException.class, () -> studentService.save(student));
    }

    @Test
    void shouldUpdateStudentInSameGroup() {
        Group group = sampleGroup(1L, 5, 10);
        Student existing = sampleStudent(1L, group);
        Student updated = sampleStudent(null, group).setLastName("Петров");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(studentRepository.save(any())).thenReturn(updated);

        Student result = studentService.update(1L, updated);
        assertEquals("Петров", result.getLastName());
    }

    @Test
    void shouldUpdateStudentAndChangeGroup() {
        Group oldGroup = sampleGroup(1L, 5, 10);
        Group newGroup = sampleGroup(2L, 3, 10);
        Student existing = sampleStudent(1L, oldGroup);
        Student updated = sampleStudent(null, newGroup);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(groupRepository.findById(2L)).thenReturn(Optional.of(newGroup));
        when(studentRepository.save(any())).thenReturn(updated);

        Student result = studentService.update(1L, updated);

        assertEquals(2L, result.getGroup().getId());
        verify(groupRepository).save(oldGroup);
        verify(groupRepository).save(newGroup);
    }

    @Test
    void shouldThrowWhenUpdatingToFullGroup() {
        Group oldGroup = sampleGroup(1L, 5, 10);
        Group fullGroup = sampleGroup(2L, 10, 10);
        Student existing = sampleStudent(1L, oldGroup);
        Student updated = sampleStudent(null, fullGroup);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(groupRepository.findById(2L)).thenReturn(Optional.of(fullGroup));

        assertThrows(IllegalStateException.class, () -> studentService.update(1L, updated));
    }

    @Test
    void shouldDeleteStudent() {
        Group group = sampleGroup(1L, 5, 10);
        Student student = sampleStudent(1L, group);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        studentService.deleteById(1L);

        verify(studentRepository).deleteById(1L);
        verify(groupRepository).save(group);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentStudent() {
        when(studentRepository.findById(123L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> studentService.deleteById(123L));
    }

    @Test
    void shouldReturnStudentsByGroupId() {
        when(studentRepository.findByGroupId(1L)).thenReturn(List.of(new Student(), new Student()));
        List<Student> students = studentService.getByGroupId(1L);
        assertEquals(2, students.size());
    }
}
