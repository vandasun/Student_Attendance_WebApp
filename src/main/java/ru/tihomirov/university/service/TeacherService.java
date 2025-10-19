package ru.tihomirov.university.service;

import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.model.Teacher;

import java.util.List;

public interface TeacherService {
    List<Teacher> getAll();
    Teacher getById(Long id);
    Teacher getByEmail(String email);
    Teacher save(Teacher teacher);
    void deleteById(Long id);
}
