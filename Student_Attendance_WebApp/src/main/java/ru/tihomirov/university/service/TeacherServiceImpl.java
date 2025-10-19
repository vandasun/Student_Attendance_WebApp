package ru.tihomirov.university.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.model.Teacher;
import ru.tihomirov.university.repository.TeacherRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherServiceImpl implements TeacherService {

    private final TeacherRepository teacherRepository;

    @Override
    public List<Teacher> getAll() {
        return teacherRepository.findAll();
    }

    @Override
    public Teacher getById(Long id) {
        return teacherRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with id: " + id));
    }

    @Override
    public Teacher getByEmail(String email) {
        return teacherRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Teacher not found with email: " + email));
    }


    @Override
    public Teacher save(Teacher teacher) {
        return teacherRepository.save(teacher);
    }

    @Override
    public void deleteById(Long id) {
        if (!teacherRepository.existsById(id)) {
            throw new EntityNotFoundException("Teacher not found with id: " + id);
        }
        teacherRepository.deleteById(id);
    }
}
