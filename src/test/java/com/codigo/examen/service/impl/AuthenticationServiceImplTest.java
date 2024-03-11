package com.codigo.examen.service.impl;

import com.codigo.examen.entity.Usuario;
import com.codigo.examen.repository.UsuarioRepository;
import com.codigo.examen.request.SignInRequest;
import com.codigo.examen.response.AuthenticationResponse;
import com.codigo.examen.service.JWTService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthenticationServiceImplTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JWTService jwtService;
    @InjectMocks
    private AuthenticationServiceImpl authenticationService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void signin_ValidCredentials_ReturnsAuthenticationResponseWithToken() {
        SignInRequest signInRequest = new SignInRequest("username", "password");
        Usuario user = new Usuario();
        user.setUsername(signInRequest.getUsername());
        String token = "token";
        when(usuarioRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn(token);
        AuthenticationResponse authenticationResponse = authenticationService.signin(signInRequest);
        assertEquals(token, authenticationResponse.getToken());
    }

    @Test
    void signin_InvalidCredentials_ThrowsIllegalArgumentException() {
        SignInRequest signInRequest = new SignInRequest("invalidusername", "password");
        when(usuarioRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            authenticationService.signin(signInRequest);
        });
        assertEquals("Usuario no valido", exception.getMessage());
    }

    @Test
    void signin_AuthenticationManagerCalledWithCorrectCredentials() {
        SignInRequest signInRequest = new SignInRequest("username", "password");
        Usuario user = new Usuario();
        user.setUsername(signInRequest.getUsername());
        when(usuarioRepository.findByUsername(signInRequest.getUsername())).thenReturn(Optional.of(user));
        authenticationService.signin(signInRequest);
        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
    }

}