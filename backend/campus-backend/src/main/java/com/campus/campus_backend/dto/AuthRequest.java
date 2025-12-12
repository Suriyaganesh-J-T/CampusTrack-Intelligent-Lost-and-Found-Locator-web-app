package com.campus.campus_backend.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor
public class AuthRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String role; // optional
}
