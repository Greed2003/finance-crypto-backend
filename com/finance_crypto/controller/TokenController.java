package com.finance_crypto.controller;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.finance_crypto.controller.dto.LoginRequestDTO;
import com.finance_crypto.controller.dto.LoginResponseDTO;
import com.finance_crypto.controller.dto.RegisterRequestDTO;
import com.finance_crypto.controller.dto.RegisterResponseDTO;
import com.finance_crypto.controller.dto.UserResponseDTO;
import com.finance_crypto.entity.User;
import com.finance_crypto.repository.UserRepository;

@RestController
public class TokenController {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenController(JwtEncoder jwtEncoder, UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {

        var user = userRepository.findByUsername(loginRequest.username());

        if (user.isEmpty() || !user.get().isLoginCorrect(loginRequest, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("Invalid credentials");
        }

        var now = Instant.now();
        var expiresIn = 800L;

        var claims = JwtClaimsSet.builder()
                .issuer("myBackend")
                .subject(user.get().getUserId().toString())
                .claim("username", user.get().getUsername())
                .claim("roles", List.of(user.get().getRole()))
                .issuedAt(now)
                .expiresAt(now.plusSeconds(expiresIn))
                .build();

        var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();

        return ResponseEntity.ok(new LoginResponseDTO(jwtValue, expiresIn));
    }

    @PostMapping("/auth/register")
    public ResponseEntity<RegisterResponseDTO> register(@RequestBody RegisterRequestDTO registerRequest) {

        if (userRepository.findByUsername(registerRequest.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.findByEmail(registerRequest.email()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = new User();
        user.setUsername(registerRequest.username());
        user.setEmail(registerRequest.email());
        user.setPassword(bCryptPasswordEncoder.encode(registerRequest.password()));
        user.setRole("ROLE_USER");

        User savedUser = userRepository.save(user);

        return ResponseEntity.ok(
                new RegisterResponseDTO(
                        savedUser.getUserId(),
                        savedUser.getUsername(),
                        savedUser.getEmail(),
                        savedUser.getRole()
                )
        );
    }

    @GetMapping("/auth/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication) {

        Jwt jwt = (Jwt) authentication.getPrincipal();
        String subject = jwt.getSubject();

        var user = userRepository.findById(UUID.fromString(subject))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                new UserResponseDTO(
                        user.getUserId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getRole()
                )
        );
    }
}
