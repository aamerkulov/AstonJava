package com.example.userservice.service;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.event.UserEvent;
import com.example.userservice.entity.User;
import com.example.userservice.exception.UserNotFoundException;
import com.example.userservice.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;
    private final CircuitBreakerFactory circuitBreakerFactory;
    public UserService(UserRepository userRepository,
                       KafkaTemplate<String, UserEvent> kafkaTemplate,
                       CircuitBreakerFactory circuitBreakerFactory) {
        this.userRepository = userRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        return circuitBreakerFactory.create("userServiceCircuitBreaker").run(
                () -> {
                    User user = userRepository.findById(id)
                            .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
                    return convertToDTO(user);
                },
                throwable -> {
                    // Fallback logic
                    return new UserResponseDTO();
                }
        );
    }

    @Transactional
    public UserResponseDTO createUser(@NotNull UserRequestDTO userRequestDTO) {
        User user = new User();
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());

        User savedUser = userRepository.save(user);
        kafkaTemplate.send("user-events", new UserEvent(
                savedUser.getEmail(),
                "CREATE",
                "Здравствуйте! Ваш аккаунт на сайте был успешно создан."
        ));
        return convertToDTO(savedUser);
    }

    @Transactional
    public UserResponseDTO updateUser(Long id, @NotNull UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setEmail(userRequestDTO.getEmail());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        kafkaTemplate.send("user-events", new UserEvent(
                user.getEmail(),
                "DELETE",
                "Здравствуйте! Ваш аккаунт был удалён"
        ));
        userRepository.deleteById(id);
    }

    private UserResponseDTO convertToDTO(@NotNull User user) {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}