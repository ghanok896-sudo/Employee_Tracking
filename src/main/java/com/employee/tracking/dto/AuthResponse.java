
package com.employee.tracking.dto;

public record AuthResponse(
    String message,
    String token,
    String tokenType,
    Long userId,
    String fullName,
    String email,
    String role
) {
}
