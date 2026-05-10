package com.loadix.infrastructure.in.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loadix.application.dto.request.LoginRequest;
import com.loadix.application.dto.request.RegisterRequest;
import com.loadix.application.dto.request.UpdateEmailRequest;
import com.loadix.application.dto.response.AuthSessionResponse;
import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.port.in.GetCurrentUserPort;
import com.loadix.application.port.in.LoginUserPort;
import com.loadix.application.port.in.RegisterUserPort;
import com.loadix.application.port.in.UpdateCurrentUserEmailPort;
import com.loadix.domain.exception.InvalidCredentialsException;
import com.loadix.infrastructure.security.AuthCookieService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth", description = "Authentication and current user operations")
public class AuthController {

    private final LoginUserPort loginUserPort;
    private final GetCurrentUserPort getCurrentUserPort;
    private final RegisterUserPort registerUserPort;
    private final UpdateCurrentUserEmailPort updateCurrentUserEmailPort;
    private final AuthCookieService authCookieService;

    public AuthController(
            LoginUserPort loginUserPort,
            GetCurrentUserPort getCurrentUserPort,
            RegisterUserPort registerUserPort,
            UpdateCurrentUserEmailPort updateCurrentUserEmailPort,
            AuthCookieService authCookieService
    ) {
        this.loginUserPort = loginUserPort;
        this.getCurrentUserPort = getCurrentUserPort;
        this.registerUserPort = registerUserPort;
        this.updateCurrentUserEmailPort = updateCurrentUserEmailPort;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    @ApiResponse(responseCode = "201", description = "User registered",
            content = @Content(schema = @Schema(implementation = AuthUserResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid registration payload")
    public ResponseEntity<AuthUserResponse> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthUserResponse user = registerUserPort.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    @Operation(summary = "Log in with email and password")
    @ApiResponse(responseCode = "200", description = "User authenticated",
            content = @Content(schema = @Schema(implementation = AuthUserResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid login payload")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthUserResponse> login(
            @Valid @RequestBody LoginRequest request,
            @Parameter(hidden = true) HttpServletResponse response
    ) {
        AuthSessionResponse session = loginUserPort.execute(request);
        authCookieService.setAuthCookie(response, session.token());
        return ResponseEntity.ok(session.user());
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user")
    @ApiResponse(responseCode = "200", description = "Current user returned",
            content = @Content(schema = @Schema(implementation = AuthUserResponse.class)))
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    public AuthUserResponse me(@Parameter(hidden = true) Authentication authentication) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }
        return getCurrentUserPort.execute(authentication.getName());
    }

    @PatchMapping("/email")
    @Operation(summary = "Update the authenticated user's email")
    @ApiResponse(responseCode = "200", description = "Email updated",
            content = @Content(schema = @Schema(implementation = AuthUserResponse.class)))
    @ApiResponse(responseCode = "400", description = "Invalid email payload")
    @ApiResponse(responseCode = "401", description = "Unauthenticated")
    public AuthUserResponse updateEmail(
            @Parameter(hidden = true) Authentication authentication,
            @Valid @RequestBody UpdateEmailRequest request,
            @Parameter(hidden = true) HttpServletResponse response
    ) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }

        AuthSessionResponse session = updateCurrentUserEmailPort.execute(authentication.getName(), request);
        authCookieService.setAuthCookie(response, session.token());
        return session.user();
    }

    @PostMapping("/logout")
    @Operation(summary = "Clear the authentication cookie")
    @ApiResponse(responseCode = "200", description = "Auth cookie cleared")
    public Void logout(@Parameter(hidden = true) HttpServletResponse response) {
        authCookieService.clearAuthCookie(response);
        return null;
    }
}
