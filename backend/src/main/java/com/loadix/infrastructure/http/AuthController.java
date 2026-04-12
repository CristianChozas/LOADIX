package com.loadix.infrastructure.http;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.loadix.application.dto.auth.AuthUserResponse;
import com.loadix.application.dto.auth.RegisterRequest;
import com.loadix.application.dto.common.ApiSuccessResponse;
import com.loadix.application.usecase.RegisterUserUseCase;

import jakarta.validation.Valid;

@Validated
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final RegisterUserUseCase registerUserUseCase;

    public AuthController(RegisterUserUseCase registerUserUseCase) {
        this.registerUserUseCase = registerUserUseCase;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiSuccessResponse<AuthUserResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        AuthUserResponse user = registerUserUseCase.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiSuccessResponse<>(true, user));
    }
}
