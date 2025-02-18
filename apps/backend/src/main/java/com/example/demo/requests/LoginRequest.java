package com.example.demo.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotBlank
    @NotNull
    private String email;
    @NotNull
    @NotBlank
    @Size(min = 8, max = 128)
    private String password;
}
