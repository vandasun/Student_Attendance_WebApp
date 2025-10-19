package ru.tihomirov.university.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.tihomirov.university.dto.AttendanceInfoDto;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.*;
import ru.tihomirov.university.repository.*;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AttendanceServiceImplTest {

    @Mock private AttendanceRepository attendanceRepository;
    @Mock private ScheduleRepository scheduleRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private AttendanceStatusRepository attendanceStatusRepository;

    @InjectMocks private AttendanceServiceImpl attendanceService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Attendance createSampleAttendance() {
        Schedule schedule = new Schedule()
                .setId(1L)
                .setGroup(new Group().setId(1L).setName("Group A"))
                .setTeacher(new Teacher().setId(1L).setLastName("Ivanov").setName("Ivan").setMiddleName("Ivanovich"))
                .setCourse(new Course().setId(1L).setName("Mathematics"))
                .setStartTime(LocalTime.of(10, 0))
                .setEndTime(LocalTime.of(11, 0));

        Student student = new Student()
                .setId(1L)
                .setLastName("Petrov")
                .setName("Petr")
                .setMiddleName("Petrovich");

        AttendanceStatus status = new AttendanceStatus()
                .setId(1L)
                .setAttendanceStatusName("Present");

        return new Attendance()
                .setId(1L)
                .setSchedule(schedule)
                .setStudent(student)
                .setAttendanceStatus(status)
                .setMarkedTime(LocalTime.of(10, 5));
    }

    private AttendanceInfoDto createSampleAttendanceInfoDto() {
        return new AttendanceInfoDto()
                .setStudentFullName("Petrov Petr Petrovich")
                .setGroupName("Group A")
                .setTeacherFullName("Ivanov Ivan Ivanovich")
                .setAttendanceStatusName("Present")
                .setMarkedTime(LocalTime.of(10, 5))
                .setScheduleStartTime(LocalTime.of(10, 0))
                .setScheduleEndTime(LocalTime.of(11, 0))
                .setCourseName("Mathematics");
    }

    @Test
    void shouldSaveAttendance() {
        Attendance attendance = createSampleAttendance();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(attendance.getSchedule()));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(attendance.getStudent()));
        when(attendanceStatusRepository.findById(1L)).thenReturn(Optional.of(attendance.getAttendanceStatus()));
        when(attendanceRepository.findByScheduleId(1L)).thenReturn(List.of());
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        Attendance saved = attendanceService.save(attendance);
        assertNotNull(saved);
        assertEquals(attendance.getId(), saved.getId());
        verify(attendanceRepository, times(1)).save(attendance);
    }

    @Test
    void shouldThrowWhenScheduleNotFoundOnSave() {
        Attendance attendance = createSampleAttendance();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attendanceService.save(attendance));
    }

    @Test
    void shouldThrowWhenStudentNotFoundOnSave() {
        Attendance attendance = createSampleAttendance();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(attendance.getSchedule()));
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attendanceService.save(attendance));
    }

    @Test
    void shouldThrowWhenAttendanceStatusNotFoundOnSave() {
        Attendance attendance = createSampleAttendance();
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(attendance.getSchedule()));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(attendance.getStudent()));
        when(attendanceStatusRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attendanceService.save(attendance));
    }

    @Test
    void shouldThrowWhenAttendanceAlreadyExists() {
        Attendance attendance = createSampleAttendance();
        Attendance existingAttendance = createSampleAttendance();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(attendance.getSchedule()));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(attendance.getStudent()));
        when(attendanceStatusRepository.findById(1L)).thenReturn(Optional.of(attendance.getAttendanceStatus()));
        when(attendanceRepository.findByScheduleId(1L)).thenReturn(List.of(existingAttendance));

        assertThrows(IllegalStateException.class, () -> attendanceService.save(attendance));
    }


    @Test
    void shouldThrowWhenAttendanceNotFoundOnUpdate() {
        Attendance updated = createSampleAttendance();
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attendanceService.update(999L, updated));
    }

    @Test
    void shouldDeleteAttendanceSuccessfully() {
        when(attendanceRepository.existsById(1L)).thenReturn(true);
        attendanceService.delete(1L);
        verify(attendanceRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenAttendanceNotFoundOnDelete() {
        when(attendanceRepository.existsById(999L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> attendanceService.delete(999L));
    }

    @Test
    void shouldGetAttendanceById() {
        Attendance attendance = createSampleAttendance();
        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(attendance));

        Attendance result = attendanceService.getById(1L);
        assertEquals(attendance.getId(), result.getId());
    }

    @Test
    void shouldThrowWhenAttendanceNotFoundOnGetById() {
        when(attendanceRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> attendanceService.getById(999L));
    }

    @Test
    void shouldGetAllAttendances() {
        List<Attendance> attendances = List.of(createSampleAttendance());
        when(attendanceRepository.findAll()).thenReturn(attendances);

        List<Attendance> result = attendanceService.getAll();
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetByScheduleId() {
        List<Attendance> attendances = List.of(createSampleAttendance());
        when(attendanceRepository.findByScheduleId(1L)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getByScheduleId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetByScheduleIdPaged() {
        List<Attendance> attendances = List.of(createSampleAttendance());
        Page<Attendance> page = new PageImpl<>(attendances);
        Pageable pageable = Pageable.unpaged();

        when(attendanceRepository.findByScheduleId(1L, pageable)).thenReturn(page);

        Page<Attendance> result = attendanceService.getByScheduleIdPaged(1L, pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldGetByStudentId() {
        List<Attendance> attendances = List.of(createSampleAttendance());
        when(attendanceRepository.findByStudentId(1L)).thenReturn(attendances);

        List<Attendance> result = attendanceService.getByStudentId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetByStudentIdPaged() {
        List<Attendance> attendances = List.of(createSampleAttendance());
        Page<Attendance> page = new PageImpl<>(attendances);
        Pageable pageable = Pageable.unpaged();

        when(attendanceRepository.findByStudentId(1L, pageable)).thenReturn(page);

        Page<Attendance> result = attendanceService.getByStudentIdPaged(1L, pageable);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void shouldGetByAttendanceStatusId() {
        List<Attendance> attendances = List.of(createSampleAttendance());
        when(attendanceRepository.findAll()).thenReturn(attendances);

        List<Attendance> result = attendanceService.getByAttendanceStatusId(1L);
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetFormattedAttendanceBySchedule() {
        List<Attendance> attendances = List.of(createSampleAttendance());
        when(attendanceRepository.findByScheduleId(1L)).thenReturn(attendances);

        List<AttendanceInfoDto> result = attendanceService.getFormattedAttendanceBySchedule(1L);
        assertEquals(1, result.size());
    }

    @Test
    void shouldGetFormattedAttendanceByStudent() {
        List<Attendance> attendances = List.of(createSampleAttendance());
        when(attendanceRepository.findByStudentId(1L)).thenReturn(attendances);

        List<AttendanceInfoDto> result = attendanceService.getFormattedAttendanceByStudent(1L);
        assertEquals(1, result.size());
    }

    @Test
    void shouldMarkAttendance() {
        Attendance attendance = createSampleAttendance();

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(attendance.getSchedule()));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(attendance.getStudent()));
        when(attendanceStatusRepository.findById(1L)).thenReturn(Optional.of(attendance.getAttendanceStatus()));
        when(attendanceRepository.findByScheduleId(1L)).thenReturn(List.of());
        when(attendanceRepository.save(any(Attendance.class))).thenReturn(attendance);

        Attendance result = attendanceService.markAttendance(1L, 1L, 1L, 1L);
        assertNotNull(result);
        assertNotNull(result.getMarkedTime());
    }

    @Test
    void shouldMarkAttendanceForGroup() {
        Schedule schedule = createSampleAttendance().getSchedule();
        AttendanceStatus status = createSampleAttendance().getAttendanceStatus();
        List<Long> studentIds = List.of(1L, 2L);
        Student student1 = new Student().setId(1L);
        Student student2 = new Student().setId(2L);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(attendanceStatusRepository.findById(1L)).thenReturn(Optional.of(status));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student1));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(student2));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Attendance> result = attendanceService.markAttendanceForGroup(1L, 1L, studentIds, 1L);
        assertEquals(2, result.size());
        verify(attendanceRepository, times(2)).save(any(Attendance.class));
    }

    @Test
    void shouldThrowWhenStudentNotFoundInGroupMarking() {
        Schedule schedule = createSampleAttendance().getSchedule();
        AttendanceStatus status = createSampleAttendance().getAttendanceStatus();
        List<Long> studentIds = List.of(1L, 999L);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(attendanceStatusRepository.findById(1L)).thenReturn(Optional.of(status));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(new Student().setId(1L)));
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> attendanceService.markAttendanceForGroup(1L, 1L, studentIds, 1L));
    }

    @Test
    void shouldSetCurrentTimeWhenMarkedTimeIsNull() {
        Attendance attendance = createSampleAttendance().setMarkedTime(null);

        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(attendance.getSchedule()));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(attendance.getStudent()));
        when(attendanceStatusRepository.findById(1L)).thenReturn(Optional.of(attendance.getAttendanceStatus()));
        when(attendanceRepository.findByScheduleId(1L)).thenReturn(List.of());
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> {
            Attendance saved = invocation.getArgument(0);
            assertNotNull(saved.getMarkedTime());
            return saved;
        });

        attendanceService.save(attendance);
    }

    @Test
    void shouldUpdateOnlyScheduleWhenProvided() {
        // Given
        Attendance existing = createSampleAttendance();
        Attendance updated = new Attendance(); // создаем пустой объект обновления
        Schedule newSchedule = new Schedule().setId(2L);
        updated.setSchedule(newSchedule);

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(scheduleRepository.findById(2L)).thenReturn(Optional.of(newSchedule));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Attendance result = attendanceService.update(1L, updated);

        // Then
        verify(scheduleRepository, times(1)).findById(2L);
        verify(attendanceRepository, times(1)).save(existing);
        assertEquals(newSchedule, existing.getSchedule());
        // Проверяем, что остальные поля не изменились
        assertEquals(createSampleAttendance().getStudent(), existing.getStudent());
        assertEquals(createSampleAttendance().getAttendanceStatus(), existing.getAttendanceStatus());
        assertEquals(createSampleAttendance().getMarkedTime(), existing.getMarkedTime());
    }

    @Test
    void shouldUpdateOnlyStudentWhenProvided() {
        // Given
        Attendance existing = createSampleAttendance();
        Attendance updated = new Attendance();
        Student newStudent = new Student().setId(2L).setLastName("Sidorov").setName("Sidor").setMiddleName("Sidorovich");
        updated.setStudent(newStudent);

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(newStudent));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Attendance result = attendanceService.update(1L, updated);

        // Then
        verify(studentRepository, times(1)).findById(2L);
        verify(attendanceRepository, times(1)).save(existing);
        assertEquals(newStudent, existing.getStudent());
        // Проверяем, что остальные поля не изменились
        assertEquals(createSampleAttendance().getSchedule(), existing.getSchedule());
        assertEquals(createSampleAttendance().getAttendanceStatus(), existing.getAttendanceStatus());
        assertEquals(createSampleAttendance().getMarkedTime(), existing.getMarkedTime());
    }

    @Test
    void shouldUpdateOnlyAttendanceStatusWhenProvided() {
        // Given
        Attendance existing = createSampleAttendance();
        Attendance updated = new Attendance();
        AttendanceStatus newStatus = new AttendanceStatus().setId(2L).setAttendanceStatusName("Absent");
        updated.setAttendanceStatus(newStatus);

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(attendanceStatusRepository.findById(2L)).thenReturn(Optional.of(newStatus));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Attendance result = attendanceService.update(1L, updated);

        // Then
        verify(attendanceStatusRepository, times(1)).findById(2L);
        verify(attendanceRepository, times(1)).save(existing);
        assertEquals(newStatus, existing.getAttendanceStatus());
        // Проверяем, что остальные поля не изменились
        assertEquals(createSampleAttendance().getSchedule(), existing.getSchedule());
        assertEquals(createSampleAttendance().getStudent(), existing.getStudent());
        assertEquals(createSampleAttendance().getMarkedTime(), existing.getMarkedTime());
    }

    @Test
    void shouldUpdateOnlyMarkedTimeWhenProvided() {
        // Given
        Attendance existing = createSampleAttendance();
        Attendance updated = new Attendance();
        LocalTime newMarkedTime = LocalTime.of(11, 30);
        updated.setMarkedTime(newMarkedTime);

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Attendance result = attendanceService.update(1L, updated);

        // Then
        // Не должно быть вызовов к репозиториям сущностей, т.к. обновляется только время
        verify(scheduleRepository, never()).findById(any());
        verify(studentRepository, never()).findById(any());
        verify(attendanceStatusRepository, never()).findById(any());
        verify(attendanceRepository, times(1)).save(existing);
        assertEquals(newMarkedTime, existing.getMarkedTime());
        // Проверяем, что остальные поля не изменились
        assertEquals(createSampleAttendance().getSchedule(), existing.getSchedule());
        assertEquals(createSampleAttendance().getStudent(), existing.getStudent());
        assertEquals(createSampleAttendance().getAttendanceStatus(), existing.getAttendanceStatus());
    }

    @Test
    void shouldUpdateAllFieldsWhenAllProvided() {
        // Given
        Attendance existing = createSampleAttendance();
        Attendance updated = new Attendance();

        Schedule newSchedule = new Schedule().setId(2L);
        Student newStudent = new Student().setId(2L);
        AttendanceStatus newStatus = new AttendanceStatus().setId(2L);
        LocalTime newMarkedTime = LocalTime.of(11, 30);

        updated.setSchedule(newSchedule)
                .setStudent(newStudent)
                .setAttendanceStatus(newStatus)
                .setMarkedTime(newMarkedTime);

        when(attendanceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(scheduleRepository.findById(2L)).thenReturn(Optional.of(newSchedule));
        when(studentRepository.findById(2L)).thenReturn(Optional.of(newStudent));
        when(attendanceStatusRepository.findById(2L)).thenReturn(Optional.of(newStatus));
        when(attendanceRepository.save(any(Attendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Attendance result = attendanceService.update(1L, updated);

        // Then
        verify(scheduleRepository, times(1)).findById(2L);
        verify(studentRepository, times(1)).findById(2L);
        verify(attendanceStatusRepository, times(1)).findById(2L);
        verify(attendanceRepository, times(1)).save(existing);

        assertEquals(newSchedule, existing.getSchedule());
        assertEquals(newStudent, existing.getStudent());
        assertEquals(newStatus, existing.getAttendanceStatus());
        assertEquals(newMarkedTime, existing.getMarkedTime());
    }
}