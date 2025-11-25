package com.campus.campus_backend.dto;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AuthRequest {
    private String name;
    private String email;
    private String password;
    private String role; // optional for register
}
