package ru.tihomirov.university.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.tihomirov.university.model.Group;
import ru.tihomirov.university.service.GroupService;

import java.util.List;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupController {

    private final GroupService groupService;

    @GetMapping
    public ResponseEntity<List<Group>> getAll() {
        return ResponseEntity.ok(groupService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getById(@PathVariable Long id) {
        return ResponseEntity.ok(groupService.getById(id));
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<Group> getByName(@PathVariable String name) {
        return ResponseEntity.ok(groupService.getByName(name));
    }

    @PostMapping
    public ResponseEntity<Group> create(@RequestBody Group group) {
        return ResponseEntity.ok(groupService.save(group));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Group> update(@PathVariable Long id, @RequestBody Group updatedGroup) {
        return ResponseEntity.ok(groupService.update(id, updatedGroup));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        groupService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
