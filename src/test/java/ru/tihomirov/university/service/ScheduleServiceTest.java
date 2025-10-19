package ru.tihomirov.university.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.*;
import ru.tihomirov.university.repository.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    @Mock private ScheduleRepository scheduleRepository;
    @Mock private GroupRepository groupRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private TeacherRepository teacherRepository;
    @Mock private ClassTypeRepository classTypeRepository;

    @InjectMocks private ScheduleServiceImpl scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Schedule createSampleSchedule() {
        return new Schedule()
                .setId(1L)
                .setGroup(new Group().setId(1L))
                .setCourse(new Course().setId(2L))
                .setTeacher(new Teacher().setId(3L))
                .setClassType(new ClassType().setId(4L))
                .setDate(LocalDate.now())
                .setStartTime(LocalTime.of(10, 0))
                .setEndTime(LocalTime.of(11, 0));
    }

    @Test
    void shouldSaveSchedule() {
        Schedule s = createSampleSchedule();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(new Group()));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(new Course()));
        when(teacherRepository.findById(3L)).thenReturn(Optional.of(new Teacher()));
        when(classTypeRepository.findById(4L)).thenReturn(Optional.of(new ClassType()));
        when(scheduleRepository.existsByGroupIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any(), any())).thenReturn(false);
        when(scheduleRepository.existsByTeacherIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any(), any())).thenReturn(false);
        when(scheduleRepository.save(any())).thenReturn(s);

        Schedule saved = scheduleService.save(s);
        assertEquals(s.getDate(), saved.getDate());
        verify(scheduleRepository, times(1)).save(s);
    }

    @Test
    void shouldThrowWhenScheduleNotFound() {
        when(scheduleRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> scheduleService.getById(100L));
    }

    @Test
    void shouldDeleteScheduleSuccessfully() {
        when(scheduleRepository.existsById(1L)).thenReturn(true);
        scheduleService.delete(1L);
        verify(scheduleRepository).deleteById(1L);
    }

    @Test
    void shouldUpdateSchedule() {
        Schedule existing = createSampleSchedule();
        Schedule updated = createSampleSchedule().setDate(LocalDate.now().plusDays(1));

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(groupRepository.findById(any())).thenReturn(Optional.of(new Group()));
        when(courseRepository.findById(any())).thenReturn(Optional.of(new Course()));
        when(teacherRepository.findById(any())).thenReturn(Optional.of(new Teacher()));
        when(classTypeRepository.findById(any())).thenReturn(Optional.of(new ClassType()));
        when(scheduleRepository.findByGroupId(any())).thenReturn(List.of());
        when(scheduleRepository.findByTeacherId(any())).thenReturn(List.of());
        when(scheduleRepository.save(any())).thenReturn(updated);

        Schedule result = scheduleService.update(1L, updated);
        assertEquals(updated.getDate(), result.getDate());
    }

    @Test
    void shouldThrowWhenGroupConflict() {
        Schedule s = createSampleSchedule();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(new Group()));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(new Course()));
        when(teacherRepository.findById(3L)).thenReturn(Optional.of(new Teacher()));
        when(classTypeRepository.findById(4L)).thenReturn(Optional.of(new ClassType()));
        when(scheduleRepository.existsByGroupIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any(), any()))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> scheduleService.save(s));
    }

    @Test
    void shouldThrowWhenTeacherConflict() {
        Schedule s = createSampleSchedule();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(new Group()));
        when(courseRepository.findById(2L)).thenReturn(Optional.of(new Course()));
        when(teacherRepository.findById(3L)).thenReturn(Optional.of(new Teacher()));
        when(classTypeRepository.findById(4L)).thenReturn(Optional.of(new ClassType()));
        when(scheduleRepository.existsByGroupIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any(), any()))
                .thenReturn(false);
        when(scheduleRepository.existsByTeacherIdAndDateAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any(), any()))
                .thenReturn(true);

        assertThrows(IllegalStateException.class, () -> scheduleService.save(s));
    }

    @Test
    void shouldGetScheduleById() {
        Schedule s = createSampleSchedule();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(s));

        Schedule result = scheduleService.getById(1L);
        assertEquals(s.getId(), result.getId());
    }

    // NEW TESTS

    @Test
    void shouldThrowWhenScheduleToUpdateNotFound() {
        Schedule update = createSampleSchedule();
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> scheduleService.update(999L, update));
    }

    @Test
    void shouldThrowWhenScheduleToDeleteNotFound() {
        when(scheduleRepository.existsById(999L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> scheduleService.delete(999L));
    }

    @Test
    void shouldThrowWhenGroupNotFoundOnSave() {
        Schedule s = createSampleSchedule();
        when(groupRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> scheduleService.save(s));
    }
    
}
