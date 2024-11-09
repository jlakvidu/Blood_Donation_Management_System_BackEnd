package edu.icet.dto;

import lombok.Data;

@Data
public class LoginRequest {
    private String emailAddress;
    private String password;
}
