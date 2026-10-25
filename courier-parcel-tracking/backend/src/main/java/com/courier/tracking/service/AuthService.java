package com.courier.tracking.service;

import com.courier.tracking.dto.AuthResponse;
import com.courier.tracking.dto.LoginRequest;
import com.courier.tracking.dto.RegisterRequest;
import com.courier.tracking.exception.DuplicateResourceException;
import com.courier.tracking.model.UserRole;
import com.courier.tracking.exception.ResourceNotFoundException;
import com.courier.tracking.model.User;
import com.courier.tracking.repository.UserRepository;
import com.courier.tracking.security.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase(java.util.Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("An account with this email already exists");
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPhone(request.phone() == null || request.phone().isBlank() ? null : request.phone().trim());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        // Public registration can only create CUSTOMER accounts. Admin/driver roles must be assigned securely.
        user.setRole(UserRole.CUSTOMER);
        User saved = userRepository.save(user);

        String token = jwtService.generateToken(saved.getUserId(), saved.getEmail(), saved.getRole().name());
        return new AuthResponse(token, saved.getUserId(), saved.getName(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtService.generateToken(
            user.getUserId(),
            user.getEmail(),
            user.getRole().name()
        );

        return new AuthResponse(
            token,
            user.getUserId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        );
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
