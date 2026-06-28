package com.finance_crypto.controller.dto;

import java.util.UUID;

public record MeResponseDTO(UUID userId, String username, String email) {
}
