package ru.tihomirov.university.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.tihomirov.university.model.ClassType;
import ru.tihomirov.university.repository.ClassTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClassTypeServiceImpl implements ClassTypeService {

    private final ClassTypeRepository classtypeRepository;

    @Override
    public List<ClassType> getAll() {
        return classtypeRepository.findAll();
    }

    @Override
    public Optional<ClassType> getById(Long id) {
        return classtypeRepository.findById(id);
    }

    @Override
    public ClassType save(ClassType classtype) {
        return classtypeRepository.save(classtype);
    }

    @Override
    public void deleteById(Long id) {
        classtypeRepository.deleteById(id);
    }
}