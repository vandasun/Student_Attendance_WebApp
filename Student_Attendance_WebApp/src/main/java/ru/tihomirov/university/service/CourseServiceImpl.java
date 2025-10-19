package ru.tihomirov.university.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tihomirov.university.exception.EntityNotFoundException;
import ru.tihomirov.university.model.Course;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.repository.CourseRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Override
    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    @Override
    public Course getById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with id: " + id));
    }

    @Override
    public Course getByName(String name) {
        return courseRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with name: " + name));
    }

    @Override
    public Course save(Course course) {
        return courseRepository.save(course);
    }

    @Override
    public void deleteById(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new EntityNotFoundException("Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }
}
