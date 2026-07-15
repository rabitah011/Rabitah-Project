package com.rabitah.frontend.model;
import java.util.UUID; public record AuthResponse(String accessToken,String refreshToken,long expiresInSeconds,UserSummary user){public record UserSummary(UUID id,String loginId,String studentId,String nickname,String role,String department,String section,Integer academicYear){}}
