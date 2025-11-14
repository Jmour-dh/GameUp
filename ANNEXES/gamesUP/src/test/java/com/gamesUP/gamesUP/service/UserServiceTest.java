package com.gamesUP.gamesUP.service;

import com.gamesUP.gamesUP.dto.UserDTO;
import com.gamesUP.gamesUP.entity.User;
import com.gamesUP.gamesUP.enumeration.Role;
import com.gamesUP.gamesUP.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User(1L, "John Doe", "john.doe@example.com", "password", Role.CUSTOMER);
        User user2 = new User(2L, "Jane Doe", "jane.doe@example.com", "password", Role.ADMIN);

        when(userRepository.findAll()).thenReturn(Arrays.asList(user1, user2));

        List<UserDTO> users = userService.getAllUsers();

        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testGetUserById() {
        User user = new User(1L, "John Doe", "john.doe@example.com", "password", Role.CUSTOMER);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDTO userDTO = userService.getUserById(1L);

        assertNotNull(userDTO);
        assertEquals("John Doe", userDTO.getName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testGetUserById_notFound_throws() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserById(999L));
        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void testCreateUser() {
        UserDTO userDTO = new UserDTO("John Doe", "john.doe@example.com", "password123", Role.CUSTOMER);
        User user = new User(1L, "John Doe", "john.doe@example.com", "hashedPassword", Role.CUSTOMER);

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO createdUser = userService.createUser(userDTO);

        assertNotNull(createdUser);
        assertEquals(userDTO.getEmail(), createdUser.getEmail());
        assertEquals("hashedPassword", createdUser.getPassword()); // vérifier mot de passe encodé retourné
        verify(userRepository, times(1)).existsByEmail(userDTO.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_emailExists_throws() {
        UserDTO userDTO = new UserDTO("John Doe", "john.exists@example.com", "password123", Role.CUSTOMER);

        when(userRepository.existsByEmail(userDTO.getEmail())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDTO));
        verify(userRepository, times(1)).existsByEmail(userDTO.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUser() {
        User user = new User(1L, "John Doe", "john.doe@example.com", "password", Role.CUSTOMER);
        UserDTO userDTO = new UserDTO("Updated Name", "updated.email@example.com", "newPassword", Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO updatedUser = userService.updateUser(1L, userDTO);

        assertNotNull(updatedUser);
        assertEquals("Updated Name", updatedUser.getName());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUser_notFound_throws() {
        UserDTO userDTO = new UserDTO("Name", "email@example.com", "pwd", Role.CUSTOMER);

        when(userRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.updateUser(5L, userDTO));
        verify(userRepository, times(1)).findById(5L);
        verify(userRepository, never()).save(any());
    }

    @Test
    void testDeleteUser() {
        userService.deleteUser(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testGetUserByEmail_success() {
        User user = new User(1L, "John Doe", "john.by.email@example.com", "password", Role.CUSTOMER);

        when(userRepository.findByEmail("john.by.email@example.com")).thenReturn(Optional.of(user));

        UserDTO dto = userService.getUserByEmail("john.by.email@example.com");

        assertNotNull(dto);
        assertEquals("john.by.email@example.com", dto.getEmail());
        verify(userRepository, times(1)).findByEmail("john.by.email@example.com");
    }

    @Test
    void testGetUserByEmail_notFound_throws() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserByEmail("missing@example.com"));
        verify(userRepository, times(1)).findByEmail("missing@example.com");
    }
}
