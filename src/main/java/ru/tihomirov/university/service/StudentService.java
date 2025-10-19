package ru.tihomirov.university.service;

import ru.tihomirov.university.model.Student;

import java.util.List;

public interface StudentService {
    List<Student> getAll();
    Student getById(Long id);
    Student save(Student student);
    Student update(Long id, Student updatedStudent);
    void deleteById(Long id);
    List<Student> getByGroupId(Long groupId);
}
