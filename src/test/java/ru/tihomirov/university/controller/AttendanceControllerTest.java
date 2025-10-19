package ru.tihomirov.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.tihomirov.university.dto.AttendanceInfoDto;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.*;
import ru.tihomirov.university.repository.ScheduleRepository;
import ru.tihomirov.university.repository.StudentRepository;
import ru.tihomirov.university.security.UserDetailsImpl;
import ru.tihomirov.university.service.AttendanceService;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AttendanceControllerTest {

    @InjectMocks
    private AttendanceController attendanceController;

    @Mock
    private AttendanceService attendanceService;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private StudentRepository studentRepository;
    @Mock
    private Authentication authentication;

    private Attendance attendance;
    private Schedule schedule;
    private Student student;
    private Teacher teacher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        teacher = new Teacher()
                .setId(1L)
                .setLastName("Иванов")
                .setName("Иван")
                .setMiddleName("Иванович");

        Group group = new Group()
                .setId(1L)
                .setName("ЦПИ-21");

        Course course = new Course()
                .setId(1L)
                .setName("Математика");

        schedule = new Schedule()
                .setId(1L)
                .setGroup(group)
                .setTeacher(teacher)
                .setCourse(course)
                .setStartTime(LocalTime.of(10, 0))
                .setEndTime(LocalTime.of(11, 0));

        student = new Student()
                .setId(1L)
                .setLastName("Петров")
                .setName("Петр")
                .setMiddleName("Петрович")
                .setGroup(group);

        AttendanceStatus status = new AttendanceStatus()
                .setId(1L)
                .setAttendanceStatusName("Присутствовал");

        attendance = new Attendance()
                .setId(1L)
                .setSchedule(schedule)
                .setStudent(student)
                .setAttendanceStatus(status)
                .setMarkedTime(LocalTime.of(10, 5));
    }

    private void mockAuth(String role, Long teacherId, Long studentId) {
        User user = new User()
                .setId(10L)
                .setUsername("login")
                .setPassword("pass")
                .setRole(new Role().setName(role));

        if (teacherId != null) {
            user.setTeacher(new Teacher().setId(teacherId));
        }
        if (studentId != null) {
            user.setStudent(new Student().setId(studentId));
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // CREATE TESTS
    @Test
    void shouldCreateAttendanceAsAdmin() {
        mockAuth("ADMIN", null, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(attendanceService.save(attendance)).thenReturn(attendance);

        ResponseEntity<Attendance> response = attendanceController.create(attendance);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        verify(attendanceService).save(attendance);
    }

    @Test
    void shouldCreateAttendanceAsTeacherForOwnSchedule() {
        mockAuth("TEACHER", 1L, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(attendanceService.save(attendance)).thenReturn(attendance);

        ResponseEntity<Attendance> response = attendanceController.create(attendance);

        assertEquals(200, response.getStatusCodeValue());
        verify(attendanceService).save(attendance);
    }

    @Test
    void shouldThrowWhenStudentTriesToCreate() {
        mockAuth("STUDENT", null, 1L);

        assertThrows(AccessDeniedException.class, () -> attendanceController.create(attendance));
    }

    @Test
    void shouldThrowWhenTeacherTriesToCreateForOtherSchedule() {
        mockAuth("TEACHER", 2L, null); // teacherId = 2, but schedule teacherId = 1
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        assertThrows(AccessDeniedException.class, () -> attendanceController.create(attendance));
    }

    // UPDATE TESTS
    @Test
    void shouldUpdateAttendanceAsAdmin() {
        mockAuth("ADMIN", null, null);
        when(attendanceService.getById(1L)).thenReturn(attendance);
        when(attendanceService.update(1L, attendance)).thenReturn(attendance);

        ResponseEntity<Attendance> response = attendanceController.update(1L, attendance);

        assertEquals(200, response.getStatusCodeValue());
        verify(attendanceService).update(1L, attendance);
    }

    @Test
    void shouldUpdateAttendanceAsTeacherForOwnSchedule() {
        mockAuth("TEACHER", 1L, null);
        when(attendanceService.getById(1L)).thenReturn(attendance);
        when(attendanceService.update(1L, attendance)).thenReturn(attendance);

        ResponseEntity<Attendance> response = attendanceController.update(1L, attendance);

        assertEquals(200, response.getStatusCodeValue());
        verify(attendanceService).update(1L, attendance);
    }

    @Test
    void shouldThrowWhenStudentTriesToUpdate() {
        mockAuth("STUDENT", null, 1L);
        when(attendanceService.getById(1L)).thenReturn(attendance);

        assertThrows(AccessDeniedException.class, () -> attendanceController.update(1L, attendance));
    }

    @Test
    void shouldThrowWhenTeacherTriesToUpdateOtherSchedule() {
        mockAuth("TEACHER", 2L, null);
        when(attendanceService.getById(1L)).thenReturn(attendance);

        assertThrows(AccessDeniedException.class, () -> attendanceController.update(1L, attendance));
    }

    // DELETE TESTS
    @Test
    void shouldDeleteAttendanceAsAdmin() {
        mockAuth("ADMIN", null, null);
        when(attendanceService.getById(1L)).thenReturn(attendance);
        doNothing().when(attendanceService).delete(1L);

        ResponseEntity<Void> response = attendanceController.delete(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(attendanceService).delete(1L);
    }

    @Test
    void shouldThrowWhenStudentTriesToDelete() {
        mockAuth("STUDENT", null, 1L);
        when(attendanceService.getById(1L)).thenReturn(attendance);

        assertThrows(AccessDeniedException.class, () -> attendanceController.delete(1L));
    }

    // GET ALL TESTS
    @Test
    void shouldGetAllAsAdmin() {
        mockAuth("ADMIN", null, null);
        List<Attendance> attendances = List.of(attendance);
        when(attendanceService.getAll()).thenReturn(attendances);

        ResponseEntity<List<Attendance>> response = attendanceController.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldGetByStudentIdWhenStudent() {
        mockAuth("STUDENT", null, 1L);
        List<Attendance> attendances = List.of(attendance);
        when(attendanceService.getByStudentId(1L)).thenReturn(attendances);

        ResponseEntity<List<Attendance>> response = attendanceController.getAll();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(attendanceService).getByStudentId(1L);
    }

    // GET BY ID TESTS
    @Test
    void shouldGetByIdAsAdmin() {
        mockAuth("ADMIN", null, null);
        when(attendanceService.getById(1L)).thenReturn(attendance);

        ResponseEntity<Attendance> response = attendanceController.getById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void shouldGetByIdAsStudentForOwnAttendance() {
        mockAuth("STUDENT", null, 1L);
        when(attendanceService.getById(1L)).thenReturn(attendance);

        ResponseEntity<Attendance> response = attendanceController.getById(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldThrowWhenStudentTriesToGetOtherAttendance() {
        mockAuth("STUDENT", null, 2L); // studentId = 2, but attendance studentId = 1
        when(attendanceService.getById(1L)).thenReturn(attendance);

        assertThrows(AccessDeniedException.class, () -> attendanceController.getById(1L));
    }

    @Test
    void shouldThrowWhenTeacherTriesToGetOtherScheduleAttendance() {
        mockAuth("TEACHER", 2L, null); // teacherId = 2, but schedule teacherId = 1
        when(attendanceService.getById(1L)).thenReturn(attendance);

        assertThrows(AccessDeniedException.class, () -> attendanceController.getById(1L));
    }

    // GET BY SCHEDULE TESTS
    @Test
    void shouldGetByScheduleAsAdmin() {
        mockAuth("ADMIN", null, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        List<Attendance> attendances = List.of(attendance);
        when(attendanceService.getByScheduleId(1L)).thenReturn(attendances);

        ResponseEntity<List<Attendance>> response = attendanceController.getBySchedule(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldGetByScheduleAsTeacherForOwnSchedule() {
        mockAuth("TEACHER", 1L, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        List<Attendance> attendances = List.of(attendance);
        when(attendanceService.getByScheduleId(1L)).thenReturn(attendances);

        ResponseEntity<List<Attendance>> response = attendanceController.getBySchedule(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldThrowWhenTeacherTriesToGetByOtherSchedule() {
        mockAuth("TEACHER", 2L, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        assertThrows(AccessDeniedException.class, () -> attendanceController.getBySchedule(1L));
    }

    // GET BY STUDENT TESTS
    @Test
    void shouldGetByStudentAsAdmin() {
        mockAuth("ADMIN", null, null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        List<Attendance> attendances = List.of(attendance);
        when(attendanceService.getByStudentId(1L)).thenReturn(attendances);

        ResponseEntity<List<Attendance>> response = attendanceController.getByStudent(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldThrowWhenStudentTriesToGetOtherStudent() {
        mockAuth("STUDENT", null, 2L);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        assertThrows(AccessDeniedException.class, () -> attendanceController.getByStudent(1L));
    }

    // GET BY STATUS TESTS
    @Test
    void shouldGetByStatusAsAdmin() {
        mockAuth("ADMIN", null, null);
        List<Attendance> attendances = List.of(attendance);
        when(attendanceService.getByAttendanceStatusId(1L)).thenReturn(attendances);

        ResponseEntity<List<Attendance>> response = attendanceController.getByStatus(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldThrowWhenStudentTriesToGetByStatus() {
        mockAuth("STUDENT", null, 1L);

        assertThrows(AccessDeniedException.class, () -> attendanceController.getByStatus(1L));
    }

    // FORMATTED ATTENDANCE TESTS
    @Test
    void shouldGetFormattedBySchedule() {
        mockAuth("ADMIN", null, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        List<AttendanceInfoDto> dtos = List.of(new AttendanceInfoDto());
        when(attendanceService.getFormattedAttendanceBySchedule(1L)).thenReturn(dtos);

        ResponseEntity<List<AttendanceInfoDto>> response = attendanceController.getFormattedBySchedule(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void shouldThrowWhenFormattedByScheduleNotFound() {
        mockAuth("ADMIN", null, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(attendanceService.getFormattedAttendanceBySchedule(1L)).thenReturn(List.of());

        assertThrows(EntityNotFoundException.class, () -> attendanceController.getFormattedBySchedule(1L));
    }

    @Test
    void shouldGetFormattedByStudent() {
        mockAuth("ADMIN", null, null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        List<AttendanceInfoDto> dtos = List.of(new AttendanceInfoDto());
        when(attendanceService.getFormattedAttendanceByStudent(1L)).thenReturn(dtos);

        ResponseEntity<List<AttendanceInfoDto>> response = attendanceController.getFormattedByStudent(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    // MARK ATTENDANCE TESTS
    @Test
    void shouldMarkAttendanceAsTeacherForOwnSchedule() {
        mockAuth("TEACHER", 1L, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        when(attendanceService.markAttendance(1L, 1L, 1L, 1L)).thenReturn(attendance);

        ResponseEntity<Attendance> response = attendanceController.markAttendance(1L, 1L, 1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(attendanceService).markAttendance(1L, 1L, 1L, 1L);
    }

    @Test
    void shouldThrowWhenStudentTriesToMarkAttendance() {
        mockAuth("STUDENT", null, 1L);

        assertThrows(AccessDeniedException.class, () -> attendanceController.markAttendance(1L, 1L, 1L));
    }

    // MARK ATTENDANCE FOR GROUP TESTS
    @Test
    void shouldMarkAttendanceForGroupAsTeacher() {
        mockAuth("TEACHER", 1L, null);
        List<Long> studentIds = List.of(1L, 2L);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        List<Attendance> attendances = List.of(attendance);
        when(attendanceService.markAttendanceForGroup(1L, 1L, studentIds, 1L)).thenReturn(attendances);

        ResponseEntity<List<Attendance>> response = attendanceController.markAttendanceForGroup(1L, studentIds, 1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }

    // PAGINATION TESTS
    @Test
    void shouldGetBySchedulePaged() {
        mockAuth("ADMIN", null, null);
        when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));
        Page<Attendance> page = new PageImpl<>(List.of(attendance));
        Pageable pageable = Pageable.unpaged();
        when(attendanceService.getByScheduleIdPaged(1L, pageable)).thenReturn(page);

        ResponseEntity<Page<Attendance>> response = attendanceController.getBySchedulePaged(1L, pageable);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getContent().size());
    }

    @Test
    void shouldGetByStudentPaged() {
        mockAuth("ADMIN", null, null);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        Page<Attendance> page = new PageImpl<>(List.of(attendance));
        Pageable pageable = Pageable.unpaged();
        when(attendanceService.getByStudentIdPaged(1L, pageable)).thenReturn(page);

        ResponseEntity<Page<Attendance>> response = attendanceController.getByStudentPaged(1L, pageable);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().getContent().size());
    }

    // ENTITY NOT FOUND TESTS
    @Test
    void shouldThrowWhenScheduleNotFound() {
        mockAuth("ADMIN", null, null);
        when(scheduleRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attendanceController.getBySchedule(999L));
    }

    @Test
    void shouldThrowWhenStudentNotFound() {
        mockAuth("ADMIN", null, null);
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> attendanceController.getByStudent(999L));
    }
}