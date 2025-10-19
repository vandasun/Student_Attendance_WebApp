package ru.tihomirov.university.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.model.Teacher;
import ru.tihomirov.university.service.TeacherService;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping
    public ResponseEntity<List<Teacher>> getAll() {
        return ResponseEntity.ok(teacherService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Teacher> getById(@PathVariable Long id) {
        return ResponseEntity.ok(teacherService.getById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Teacher> getByEmail(@PathVariable String email) {
        return ResponseEntity.ok(teacherService.getByEmail(email));
    }

    @PostMapping
    public ResponseEntity<Teacher> create(@RequestBody Teacher teacher) {
        return ResponseEntity.ok(teacherService.save(teacher));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Teacher> update(@PathVariable Long id, @RequestBody Teacher updatedTeacher) {
        teacherService.getById(id); // выбросит исключение, если не найден
        updatedTeacher.setId(id);
        return ResponseEntity.ok(teacherService.save(updatedTeacher));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        teacherService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
