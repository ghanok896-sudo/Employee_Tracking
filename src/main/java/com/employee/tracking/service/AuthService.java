
package com.employee.tracking.service;

import com.employee.tracking.dto.AuthResponse;
import com.employee.tracking.dto.LoginRequest;
import com.employee.tracking.dto.RegisterRequest;
import com.employee.tracking.entity.AppUser;
import com.employee.tracking.entity.Role;
import com.employee.tracking.repository.AppUserRepository;
import com.employee.tracking.security.JwtService;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AppUserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "An account with this email already exists"
            );
        }

        AppUser user = new AppUser();
        user.setFullName(request.fullName().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));

        // Public registration must never assign privileged roles.
        user.setRole(Role.EMPLOYEE);
        user.setEnabled(true);

        AppUser saved = userRepository.save(user);

        return new AuthResponse(
            "Registration successful. Please log in.",
            null,
            null,
            saved.getId(),
            saved.getFullName(),
            saved.getEmail(),
            saved.getRole().name()
        );
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = request.email().trim().toLowerCase();

        try {
            Authentication authentication =
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                        email, request.password()
                    )
                );

            UserDetails principal =
                (UserDetails) authentication.getPrincipal();

            AppUser user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException(
                    "Invalid email or password"));

            String token = jwtService.generateToken(principal);

            return new AuthResponse(
                "Login successful",
                token,
                "Bearer",
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
            );
        } catch (BadCredentialsException ex) {
            throw new ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Invalid email or password"
            );
        }
    }
}
