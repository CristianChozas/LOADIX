package com.loadix.infrastructure.http;

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
import com.loadix.application.dto.shared.ApiSuccessResponse;
import com.loadix.application.usecase.GetCurrentUserUseCase;
import com.loadix.application.usecase.LoginUserUseCase;
import com.loadix.application.usecase.RegisterUserUseCase;
import com.loadix.infrastructure.security.AuthCookieService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final LoginUserUseCase loginUserUseCase;
    private final GetCurrentUserUseCase getCurrentUserUseCase;
    private final RegisterUserUseCase registerUserUseCase;
    private final AuthCookieService authCookieService;

    public AuthController(
            LoginUserUseCase loginUserUseCase,
            GetCurrentUserUseCase getCurrentUserUseCase,
            RegisterUserUseCase registerUserUseCase,
            AuthCookieService authCookieService
    ) {
        this.loginUserUseCase = loginUserUseCase;
        this.getCurrentUserUseCase = getCurrentUserUseCase;
        this.registerUserUseCase = registerUserUseCase;
        this.authCookieService = authCookieService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiSuccessResponse<AuthUserResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        AuthUserResponse user = registerUserUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse<>(true, user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiSuccessResponse<AuthUserResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response
    ) {
        AuthSessionResponse session = loginUserUseCase.login(request);
        authCookieService.setAuthCookie(response, session.token());
        return ResponseEntity.ok(new ApiSuccessResponse<>(true, session.user()));
    }

    @GetMapping("/me")
    public ApiSuccessResponse<AuthUserResponse> me(Authentication authentication) {
        if (authentication == null) {
            throw new com.loadix.application.exception.ApplicationAuthenticationException("Unauthorized");
        }
        return new ApiSuccessResponse<>(true, getCurrentUserUseCase.getByEmail(authentication.getName()));
    }

    @PostMapping("/logout")
    public ApiSuccessResponse<Void> logout(HttpServletResponse response) {
        authCookieService.clearAuthCookie(response);
        return new ApiSuccessResponse<>(true, null);
    }
}
