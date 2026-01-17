package life2food.backend.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import life2food.backend.model.User;
import life2food.backend.repository.UserRepository;
import life2food.backend.service.EmailService;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    public void testGoogleLogin_ExistingUser() throws Exception {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("test@example.com");
        existingUser.setFirst_name("Test");

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(existingUser));

        User loginPayload = new User();
        loginPayload.setEmail("test@example.com");

        mockMvc.perform(post("/users/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.id").value(1));

        // Should not send email for existing user
        verify(emailService, never()).sendWelcomeEmail(anyString(), anyString());
    }

    @Test
    public void testGoogleLogin_NewUser() throws Exception {
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setEmail("new@example.com");
        savedUser.setFirst_name("New");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User loginPayload = new User();
        loginPayload.setEmail("new@example.com");
        loginPayload.setFirst_name("New");

        mockMvc.perform(post("/users/auth/google")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.id").value(2));

        // Should send email for new user
        verify(emailService).sendWelcomeEmail("new@example.com", "New");
        verify(userRepository).save(any(User.class));
    }
}
