package ru.tihomirov.university.service;

import ru.tihomirov.university.model.ClassType;

import java.util.List;
import java.util.Optional;

public interface ClassTypeService {
    List<ClassType> getAll();
    Optional<ClassType> getById(Long id);
    ClassType save(ClassType classtype);
    void deleteById(Long id);
}
