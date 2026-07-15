package com.rabitah.backend.auth.dto;
import jakarta.validation.constraints.*;
public record RegisterRequest(@NotBlank String studentId,@NotBlank String legalName,@Size(min=3,max=30) String nickname,@NotBlank @Size(min=8,max=72) @Pattern(regexp="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$") String password,@NotBlank String department,@Pattern(regexp="[AB]") String section,@Min(1) @Max(4) int currentYear,String verificationCode){}
