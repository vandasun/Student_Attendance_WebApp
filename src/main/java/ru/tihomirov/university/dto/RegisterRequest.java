package ru.tihomirov.university.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private String role; // "STUDENT" или "TEACHER"
    private Long relatedId; // id студента или преподавателя
}
