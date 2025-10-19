package ru.tihomirov.university.service;

import ru.tihomirov.university.dto.RegisterRequest;
import ru.tihomirov.university.dto.RegisterResponse;
import ru.tihomirov.university.dto.UpdateUserRequest;
import ru.tihomirov.university.model.User;

import java.util.List;

public interface UserService {
    RegisterResponse registerUser(RegisterRequest request);
    User updateUser(Long userId, UpdateUserRequest request);
    void deleteUser(Long userId);
    List<User> getAllUsers();
}
