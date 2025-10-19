package ru.tihomirov.university.service;

import ru.tihomirov.university.model.Course;

import java.util.List;

public interface CourseService {
    List<Course> getAll();
    Course getById(Long id);
    Course getByName(String name);
    Course save(Course course);
    void deleteById(Long id);
}
