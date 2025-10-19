package ru.tihomirov.university.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.repository.GroupRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GroupServiceTest {

    @Mock
    private GroupRepository groupRepository;

    @InjectMocks
    private GroupServiceImpl groupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    private Group sampleGroup() {
        return new Group()
                .setId(1L)
                .setName("ЦПИ-21")
                .setMaxCountStudents(25);
    }

    @Test
    void shouldReturnAllGroups() {
        when(groupRepository.findAll()).thenReturn(List.of(sampleGroup()));

        List<Group> result = groupService.getAll();

        assertEquals(1, result.size());
        assertEquals("ЦПИ-21", result.get(0).getName());
    }

    @Test
    void shouldReturnGroupById() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(sampleGroup()));

        Group result = groupService.getById(1L);

        assertEquals("ЦПИ-21", result.getName());
    }

    @Test
    void shouldThrowWhenGroupByIdNotFound() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> groupService.getById(99L));

        assertEquals("Group not found with id: 99", ex.getMessage());
    }

    @Test
    void shouldReturnGroupByName() {
        when(groupRepository.findByName("ЦПИ-21")).thenReturn(Optional.of(sampleGroup()));

        Group result = groupService.getByName("ЦПИ-21");

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowWhenGroupByNameNotFound() {
        when(groupRepository.findByName("ЦИС-999")).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> groupService.getByName("ЦИС-999"));

        assertEquals("Group not found with name: ЦИС-999", ex.getMessage());
    }

    @Test
    void shouldSaveGroup() {
        Group group = sampleGroup();
        when(groupRepository.save(group)).thenReturn(group);

        Group result = groupService.save(group);

        assertEquals("ЦПИ-21", result.getName());
        verify(groupRepository).save(group);
    }

    @Test
    void shouldUpdateGroup() {
        Group updated = new Group()
                .setName("ЦПИ-22")
                .setMaxCountStudents(30);

        when(groupRepository.existsById(1L)).thenReturn(true);
        when(groupRepository.save(any(Group.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Group result = groupService.update(1L, updated);

        assertEquals("ЦПИ-22", result.getName());
        assertEquals(30, result.getMaxCountStudents());
        assertEquals(1L, result.getId()); // проверка, что id установлен
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentGroup() {
        when(groupRepository.existsById(77L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> groupService.update(77L, new Group()));

        assertEquals("Group not found with id: 77", ex.getMessage());
    }

    @Test
    void shouldDeleteGroup() {
        when(groupRepository.existsById(1L)).thenReturn(true);

        groupService.deleteById(1L);

        verify(groupRepository).deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonexistentGroup() {
        when(groupRepository.existsById(66L)).thenReturn(false);

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> groupService.deleteById(66L));

        assertEquals("Group not found with id: 66", ex.getMessage());
    }
}
