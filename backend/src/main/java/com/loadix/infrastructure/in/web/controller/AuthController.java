package com.loadix.infrastructure.in.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loadix.application.dto.request.LoginRequest;
import com.loadix.application.dto.request.RegisterRequest;
import com.loadix.application.dto.response.AuthSessionResponse;
import com.loadix.application.dto.response.AuthUserResponse;
import com.loadix.application.port.in.GetCurrentUserInputPort;
import com.loadix.application.port.in.LoginUserInputPort;
import com.loadix.application.port.in.RegisterUserInputPort;
import com.loadix.domain.exception.InvalidCredentialsException;
import com.loadix.infrastructure.in.web.response.ApiSuccessResponse;
import com.loadix.infrastructure.security.AuthCookieService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUserInputPort loginUserInputPort;
    private final GetCurrentUserInputPort getCurrentUserInputPort;
    private final RegisterUserInputPort registerUserInputPort;
    private final AuthCookieService authCookieService;

    public AuthController(
            LoginUserInputPort loginUserInputPort,
            GetCurrentUserInputPort getCurrentUserInputPort,
            RegisterUserInputPort registerUserInputPort,
            AuthCookieService authCookieService
    ) {
        this.loginUserInputPort = loginUserInputPort;
        this.getCurrentUserInputPort = getCurrentUserInputPort;
        this.registerUserInputPort = registerUserInputPort;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiSuccessResponse<AuthUserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthUserResponse user = registerUserInputPort.execute(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse<>(true, user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<AuthUserResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthSessionResponse session = loginUserInputPort.execute(request);
        authCookieService.setAuthCookie(response, session.token());
        return ResponseEntity.ok(new ApiSuccessResponse<>(true, session.user()));
    }

    @GetMapping("/me")
    public ApiSuccessResponse<AuthUserResponse> me(Authentication authentication) {
        if (authentication == null) {
            throw new InvalidCredentialsException();
        }
        return new ApiSuccessResponse<>(true, getCurrentUserInputPort.execute(authentication.getName()));
    }

    @PostMapping("/logout")
    public ApiSuccessResponse<Void> logout(HttpServletResponse response) {
        authCookieService.clearAuthCookie(response);
        return new ApiSuccessResponse<>(true, null);
    }
}
