package ru.tihomirov.university.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.repository.GroupRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;

    @Override
    public List<Group> getAll() {
        return groupRepository.findAll();
    }

    @Override
    public Group getById(Long id) {
        return groupRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + id));
    }

    @Override
    public Group getByName(String name) {
        return groupRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Group not found with name: " + name));
    }

    @Override
    public Group save(Group group) {
        return groupRepository.save(group);
    }

    @Override
    public Group update(Long id, Group updatedGroup) {
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException("Group not found with id: " + id);
        }
        updatedGroup.setId(id);
        return groupRepository.save(updatedGroup);
    }

    @Override
    public void deleteById(Long id) {
        if (!groupRepository.existsById(id)) {
            throw new EntityNotFoundException("Group not found with id: " + id);
        }
        groupRepository.deleteById(id);
    }
}
