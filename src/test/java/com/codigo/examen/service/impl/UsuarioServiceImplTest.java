package com.codigo.examen.service.impl;

import com.codigo.examen.entity.Usuario;
import com.codigo.examen.repository.RolRepository;
import com.codigo.examen.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UsuarioServiceImplTest {
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private RolRepository rolRepository;
    @InjectMocks
    private UsuarioServiceImpl usuarioService;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void userDetailsService_ExistingUsername_ReturnsUserDetails() {
        String username = "testUser";
        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.of(usuario));
        UserDetails userDetails = usuarioService.userDetailsService().loadUserByUsername(username);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void userDetailsService_NonExistingUsername_ThrowsUsernameNotFoundException() {
        String username = "nonExistingUser";
        when(usuarioRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> usuarioService.userDetailsService().loadUserByUsername(username));
    }

    @Test
    void createUsuario_NewUser_ReturnsResponseEntity() {
        Usuario usuario = new Usuario();
        usuario.setUsername("newUser");
        usuario.setPassword("123");
        when(usuarioRepository.findByUsername(usuario.getUsername())).thenReturn(Optional.empty());
        when(usuarioRepository.save(usuario)).thenReturn(usuario);
        ResponseEntity<Usuario> responseEntity = usuarioService.createUsuario(usuario);
        assertNotNull(responseEntity);
        assertEquals(usuario, responseEntity.getBody());
    }

    @Test
    void getUsuarioById_ExistingId_ReturnsUsuario() {
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        ResponseEntity<Usuario> responseEntity = usuarioService.getUsuarioById(id);
        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(usuario, responseEntity.getBody());
    }

    @Test
    void getUsuarioById_NonExistingId_ReturnsNotFound() {
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Usuario> responseEntity = usuarioService.getUsuarioById(id);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void updateUsuario_ExistingUsuario_ReturnsUpdatedUsuario() {
        Long id = 1L;
        Usuario existingUsuario = new Usuario();
        existingUsuario.setIdUsuario(id);
        existingUsuario.setUsername("existingUser");
        existingUsuario.setPassword("123");

        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setIdUsuario(id);
        updatedUsuario.setUsername("updatedUser");
        updatedUsuario.setPassword("123");

        when(usuarioRepository.findById(id)).thenReturn(Optional.of(existingUsuario));
        when(usuarioRepository.findByUsername(updatedUsuario.getUsername())).thenReturn(Optional.empty());
        when(usuarioRepository.save(updatedUsuario)).thenReturn(updatedUsuario);

        ResponseEntity<Usuario> responseEntity = usuarioService.updateUsuario(id, updatedUsuario);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(updatedUsuario, responseEntity.getBody());
    }

    @Test
    void updateUsuario_NonExistingUsuario_ReturnsNotFound() {
        Long id = 1L;
        Usuario updatedUsuario = new Usuario();
        updatedUsuario.setIdUsuario(id);
        updatedUsuario.setUsername("updatedUser");
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Usuario> responseEntity = usuarioService.updateUsuario(id, updatedUsuario);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
    }

    @Test
    void deleteUsuario_ExistingUsuario_ReturnsNoContent() {
        Long id = 1L;
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(id);
        when(usuarioRepository.findById(id)).thenReturn(Optional.of(usuario));
        ResponseEntity<Usuario> responseEntity = usuarioService.deleteUsuario(id);
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        verify(usuarioRepository, times(1)).delete(usuario);
    }

    @Test
    void deleteUsuario_NonExistingUsuario_ReturnsNotFound() {
        Long id = 1L;
        when(usuarioRepository.findById(id)).thenReturn(Optional.empty());
        ResponseEntity<Usuario> responseEntity = usuarioService.deleteUsuario(id);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        verify(usuarioRepository, never()).delete(any());
    }
}