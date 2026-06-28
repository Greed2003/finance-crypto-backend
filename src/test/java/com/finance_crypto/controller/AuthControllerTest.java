package com.finance_crypto.controller;

import com.finance_crypto.controller.dto.MeResponseDTO;
import com.finance_crypto.controller.dto.RegisterRequestDTO;
import com.finance_crypto.controller.dto.RegisterResponseDTO;
import com.finance_crypto.entity.User;
import com.finance_crypto.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        // Inicializa os mocks (o dublê do repositório e do encriptador)
        MockitoAnnotations.openMocks(this);
    }

    // ==========================================
    // TESTES DO ENDPOINT POST /users (Register)
    // ==========================================

    @Test
    void deveRetornarCreatedAoRegistrarNovoUsuarioComSucesso() {
        // Arrange
        RegisterRequestDTO requestDto = new RegisterRequestDTO("fernando", "fer@teste.com", "senha123");
        
        // Simula que o banco não encontrou ninguém com esse username
        when(userRepository.findByUsername(requestDto.username())).thenReturn(Optional.empty());
        // Simula a criptografia da senha
        when(passwordEncoder.encode(requestDto.password())).thenReturn("hash_seguro_da_senha");

        // Act
        ResponseEntity<RegisterResponseDTO> response = authController.register(requestDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository, times(1)).save(any(User.class)); // Garante que mandou salvar no banco
    }

    @Test
    void deveRetornarConflictAoTentarRegistrarUsuarioJaExistente() {
        // Arrange
        RegisterRequestDTO requestDto = new RegisterRequestDTO("felipe", "felipe@teste.com", "senha123");
        
        // Simula que o banco JÁ encontrou um usuário com esse username
        when(userRepository.findByUsername(requestDto.username())).thenReturn(Optional.of(new User()));

        // Act
        ResponseEntity<RegisterResponseDTO> response = authController.register(requestDto);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any(User.class)); // Garante que NÃO tentou salvar no banco
    }

    // ==========================================
    // TESTES DO ENDPOINT GET /users/me (Me)
    // ==========================================

    @Test
    void deveRetornarDadosDoUsuarioLogadoComSucesso() {
        // Arrange
        UUID userId = UUID.randomUUID();
        
        // Cria um mock do Token JWT para simular o usuário logado
        JwtAuthenticationToken authToken = mock(JwtAuthenticationToken.class);
        when(authToken.getName()).thenReturn(userId.toString());

        // Cria o usuário que o banco vai retornar
        User mockUser = new User();
        mockUser.setUserId(userId);
        mockUser.setUsername("fernando");
        mockUser.setEmail("fer@teste.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));

        // Act
        ResponseEntity<MeResponseDTO> response = authController.me(authToken);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("fernando", response.getBody().username());
        assertEquals("fer@teste.com", response.getBody().email());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioDoTokenNaoExistirNoBanco() {
        // Arrange
        UUID userId = UUID.randomUUID();
        JwtAuthenticationToken authToken = mock(JwtAuthenticationToken.class);
        when(authToken.getName()).thenReturn(userId.toString());

        // Simula o banco retornando vazio para aquele ID
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        // Verifica se o Controller joga o erro 404 (Not Found)
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            authController.me(authToken);
        });
        
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}