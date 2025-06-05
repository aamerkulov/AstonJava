package com.example.userservice.controller;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.service.UserService;
import jakarta.validation.Valid;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @Operation(summary = "Get all users", description = "Returns a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public ResponseEntity<CollectionModel<EntityModel<UserResponseDTO>>> getAllUsers() {
        List<UserResponseDTO> users = userService.getAllUsers();

        List<EntityModel<UserResponseDTO>> userModels = users.stream()
                .map(user -> EntityModel.of(user,
                        WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                        WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getAllUsers()).withRel("users")))
                .collect(Collectors.toList());

        CollectionModel<EntityModel<UserResponseDTO>> collectionModel = CollectionModel.of(userModels);
        collectionModel.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());

        return ResponseEntity.ok(collectionModel);
    }
    @Operation(summary = "Get user by ID", description = "Returns a single user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> getUserById(@PathVariable Long id) {
        UserResponseDTO user = userService.getUserById(id);

        EntityModel<UserResponseDTO> resource = EntityModel.of(user);
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).updateUser(id, null)).withRel("update"));
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).deleteUser(id)).withRel("delete"));

        return ResponseEntity.ok(resource);
    }
    @Operation(summary = "Create a new user", description = "Creates a new user and returns the created user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<EntityModel<UserResponseDTO>> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User object to be created", required = true)
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO createdUser = userService.createUser(userRequestDTO);

        EntityModel<UserResponseDTO> resource = EntityModel.of(createdUser);
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getUserById(createdUser.getId())).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<UserResponseDTO>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);

        EntityModel<UserResponseDTO> resource = EntityModel.of(updatedUser);
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getUserById(id)).withSelfRel());
        resource.add(WebMvcLinkBuilder.linkTo(methodOn(UserController.class).getAllUsers()).withRel("all-users"));

        return ResponseEntity.ok(resource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}