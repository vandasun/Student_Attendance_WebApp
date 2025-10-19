package ru.tihomirov.university.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.model.Schedule;
import ru.tihomirov.university.model.Teacher;
import ru.tihomirov.university.model.User;
import ru.tihomirov.university.repository.GroupRepository;
import ru.tihomirov.university.repository.TeacherRepository;
import ru.tihomirov.university.security.UserDetailsImpl;
import ru.tihomirov.university.service.ScheduleService;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ScheduleControllerTest {

    @InjectMocks
    private ScheduleController scheduleController;

    @Mock
    private ScheduleService scheduleService;
    @Mock
    private GroupRepository groupRepository;
    @Mock
    private TeacherRepository teacherRepository;
    @Mock
    private Authentication authentication;

    private Schedule schedule;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Group group = new Group().setId(1L).setName("ЦПИ-21");
        Teacher teacher = new Teacher().setId(2L).setLastName("Иванов");

        schedule = new Schedule()
                .setId(1L)
                .setGroup(group)
                .setTeacher(teacher)
                .setDate(LocalDate.of(2025, 6, 25))
                .setStartTime(LocalTime.of(10, 0))
                .setEndTime(LocalTime.of(11, 0));
    }

    private void mockAuth(String role, Long teacherId) {
        User user = new User()
                .setId(10L)
                .setUsername("login")
                .setPassword("pass")
                .setRole(new ru.tihomirov.university.model.Role().setName(role));
        if (teacherId != null) {
            user.setTeacher(new Teacher().setId(teacherId));
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void shouldReturnScheduleById() {
        mockAuth("ADMIN", null);
        when(scheduleService.getById(1L)).thenReturn(schedule);

        ResponseEntity<Schedule> response = scheduleController.getById(1L);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void shouldCreateScheduleAsAdmin() {
        mockAuth("ADMIN", null);
        when(scheduleService.save(schedule)).thenReturn(schedule);

        ResponseEntity<Schedule> response = scheduleController.create(schedule);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1L, response.getBody().getId());
        verify(scheduleService).save(schedule);
    }

    @Test
    void shouldUpdateScheduleAsAdmin() {
        mockAuth("ADMIN", null);
        when(scheduleService.update(1L, schedule)).thenReturn(schedule);

        ResponseEntity<Schedule> response = scheduleController.update(1L, schedule);

        assertEquals(200, response.getStatusCodeValue());
        verify(scheduleService).update(1L, schedule);
    }

    @Test
    void shouldDeleteScheduleAsAdmin() {
        mockAuth("ADMIN", null);
        when(scheduleService.getById(1L)).thenReturn(schedule);
        doNothing().when(scheduleService).delete(1L);

        ResponseEntity<Void> response = scheduleController.delete(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(scheduleService).delete(1L);
    }
}
