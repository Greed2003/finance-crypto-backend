package com.finance_crypto.controller.dto;

import java.util.UUID;

public record UserResponseDTO(
        UUID userId,
        String username,
        String email,
        String role
) {
}
