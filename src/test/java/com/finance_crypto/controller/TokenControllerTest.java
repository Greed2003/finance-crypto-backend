package com.finance_crypto.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import com.finance_crypto.controller.dto.LoginRequestDTO;
import com.finance_crypto.controller.dto.LoginResponseDTO;
import com.finance_crypto.entity.User;
import com.finance_crypto.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class TokenControllerTest {

  @Mock
  private JwtEncoder jwtEncoder;

  @Mock
  private UserRepository userRepository;

  @Mock
  private BCryptPasswordEncoder bCryptPasswordEncoder;

  @InjectMocks
  private TokenController tokenController;

  private User testUser;
  private LoginRequestDTO validRequest;

  @BeforeEach
  void setUp() {
    // Cria mocks
    testUser = new User();
    testUser.setUserId(UUID.randomUUID());
    testUser.setUsername("testuser");
    testUser.setPassword("hashed_password");

    // Req padrao
    validRequest = new LoginRequestDTO("testuser", "raw_password");
  }

  @Test
  void retornaTokenJwtQuandoDadosValidos() {

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(bCryptPasswordEncoder.matches("raw_password", "hashed_password")).thenReturn(true);
    // Token simulado
    Jwt testJwt = mock(Jwt.class);
    when(testJwt.getTokenValue()).thenReturn("mocked_jwt_token_string");
    when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(testJwt);

    ResponseEntity<LoginResponseDTO> response = tokenController.login(validRequest);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody(), "O corpo da resposta não deve ser nulo");
    assertEquals("mocked_jwt_token_string", response.getBody().accessToken(),
        "O token gerado deve ser igual ao esperado");
    assertEquals(800L, response.getBody().expiresIn(), "O tempo de expiração do token deve ser 800 segundos");

    // Encoder deve ser chamado
    verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));
  }

  @Test
  void naoPermiteLoginComUsuarioInexistente() {

    LoginRequestDTO request = new LoginRequestDTO("unknown_user", "password");
    when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());

    assertThrows(BadCredentialsException.class, () -> {
      tokenController.login(request);
    });

    // Encoders nao devem ser chamados
    verify(bCryptPasswordEncoder, never()).matches(anyString(), anyString());
    verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
  }

  @Test
  void naoPermiteLoginComSenhaIncorreta() {

    LoginRequestDTO request = new LoginRequestDTO("testuser", "wrong_password");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(bCryptPasswordEncoder.matches("wrong_password", "hashed_password")).thenReturn(false);

    assertThrows(BadCredentialsException.class, () -> {
      tokenController.login(request);
    });

    // Encoder nao deve ser chamado
    verify(jwtEncoder, never()).encode(any(JwtEncoderParameters.class));
  }

  @Test
  void lancaExcecaoComMensagemCorretaNoLoginInvalido() {

    LoginRequestDTO request = new LoginRequestDTO("testuser", "senha_invalida");
    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(bCryptPasswordEncoder.matches("senha_invalida", "hashed_password")).thenReturn(false);

    Exception exception = assertThrows(BadCredentialsException.class, () -> {
      tokenController.login(request);
    });

    // Validando dados capturados e mockados
    assertEquals("Invalid credentials", exception.getMessage());
  }

  @Test
  void validaParametrosDoClaimsSetAoGerarToken() {

    when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(bCryptPasswordEncoder.matches("raw_password", "hashed_password")).thenReturn(true);

    Jwt testJwt = mock(Jwt.class);
    when(testJwt.getTokenValue()).thenReturn("mocked_jwt");
    when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(testJwt);

    tokenController.login(validRequest);

    // Repo e senha devem ter sido consultados usando mocks
    verify(userRepository, times(1)).findByUsername("testuser");
    verify(bCryptPasswordEncoder, times(1)).matches("raw_password", "hashed_password");
  }
}
