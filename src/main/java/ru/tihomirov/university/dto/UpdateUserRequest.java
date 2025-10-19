package ru.tihomirov.university.dto;

import lombok.Data;

@Data
public class UpdateUserRequest {
    private String username;
    private String password;
    private String role;
    private Long relatedId;
}
