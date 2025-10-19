package ru.tihomirov.university.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.model.Student;
import ru.tihomirov.university.repository.GroupRepository;
import ru.tihomirov.university.repository.StudentRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final GroupRepository groupRepository;

    @Override
    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student getById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
    }

    @Override
    @Transactional
    public Student save(Student student) {
        Group group = groupRepository.findById(student.getGroup().getId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + student.getGroup().getId()));

        if (group.getCountStudents() >= group.getMaxCountStudents()) {
            throw new IllegalStateException("Group is full: " + group.getName());
        }

        student.setGroup(group);
        Student savedStudent = studentRepository.save(student);

        group.setCountStudents(group.getCountStudents() + 1);
        groupRepository.save(group);

        return savedStudent;
    }

    @Override
    @Transactional
    public Student update(Long id, Student updatedStudent) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        Group oldGroup = existingStudent.getGroup();

        Group newGroup = groupRepository.findById(updatedStudent.getGroup().getId())
                .orElseThrow(() -> new EntityNotFoundException("Group not found with id: " + updatedStudent.getGroup().getId()));

        updatedStudent.setId(id);
        updatedStudent.setGroup(newGroup);

        // если группа изменилась — пересчитать количество
        if (!oldGroup.getId().equals(newGroup.getId())) {
            if (newGroup.getCountStudents() >= newGroup.getMaxCountStudents()) {
                throw new IllegalStateException("New group is full: " + newGroup.getName());
            }

            // сохранить студента с новой группой
            studentRepository.save(updatedStudent);

            // изменить count студентов
            oldGroup.setCountStudents(oldGroup.getCountStudents() - 1);
            newGroup.setCountStudents(newGroup.getCountStudents() + 1);

            groupRepository.save(oldGroup);
            groupRepository.save(newGroup);

            return updatedStudent;
        }

        return studentRepository.save(updatedStudent);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Student not found with id: " + id));
        Group group = student.getGroup();
        group.setCountStudents(group.getCountStudents() - 1);
        groupRepository.save(group);
        studentRepository.deleteById(id);
    }

    @Override
    public List<Student> getByGroupId(Long groupId) {
        return studentRepository.findByGroupId(groupId);
    }
}
