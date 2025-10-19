package ru.tihomirov.university.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Course;
import ru.tihomirov.university.repository.CourseRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Course sampleCourse() {
        return new Course()
                .setId(1L)
                .setName("Математика");
    }

    @Test
    void shouldReturnAllCourses() {
        when(courseRepository.findAll()).thenReturn(List.of(sampleCourse()));

        List<Course> result = courseService.getAll();

        assertEquals(1, result.size());
        assertEquals("Математика", result.get(0).getName());
    }

    @Test
    void shouldReturnCourseById() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(sampleCourse()));

        Course result = courseService.getById(1L);

        assertEquals("Математика", result.getName());
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenCourseByIdNotFound() {
        when(courseRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> courseService.getById(99L));

        assertEquals("Course not found with id: 99", ex.getMessage());
    }

    @Test
    void shouldReturnCourseByName() {
        when(courseRepository.findByName("Математика")).thenReturn(Optional.of(sampleCourse()));

        Course result = courseService.getByName("Математика");

        assertEquals(1L, result.getId());
        assertEquals("Математика", result.getName());
    }

    @Test
    void shouldThrowWhenCourseByNameNotFound() {
        when(courseRepository.findByName("Ботаника")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> courseService.getByName("Ботаника"));

        assertEquals("Course not found with name: Ботаника", ex.getMessage());
    }

    @Test
    void shouldSaveCourse() {
        Course course = sampleCourse();

        when(courseRepository.save(course)).thenReturn(course);

        Course result = courseService.save(course);

        assertEquals("Математика", result.getName());
        verify(courseRepository).save(course);
    }

    @Test
    void shouldDeleteCourseSuccessfully() {
        when(courseRepository.existsById(1L)).thenReturn(true);

        courseService.deleteById(1L);

        verify(courseRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentCourse() {
        when(courseRepository.existsById(77L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> courseService.deleteById(77L));

        assertEquals("Course not found with id: 77", ex.getMessage());
    }
}
