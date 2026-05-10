package com.loadix.infrastructure.exception;

import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import com.loadix.domain.exception.InvalidCredentialsException;
import com.loadix.domain.exception.ProfileNotFoundException;
import com.loadix.domain.exception.UserAlreadyExistsException;
import com.loadix.domain.exception.UserNotFoundException;
import com.loadix.infrastructure.http.error.RateLimitExceededException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.BAD_REQUEST, request, exception);
        List<ApiErrorResponse.FieldViolation> violations = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::mapFieldError)
                .toList();

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", request, violations);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.BAD_REQUEST, request, exception);
        return build(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(
            UserAlreadyExistsException exception,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.CONFLICT, request, exception);
        return build(HttpStatus.CONFLICT, "CONFLICT", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleAuthentication(
            InvalidCredentialsException exception,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.UNAUTHORIZED, request, exception);
        return build(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFound(
            UserNotFoundException exception,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.NOT_FOUND, request, exception);
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(ProfileNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleProfileNotFound(
            ProfileNotFoundException exception,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.NOT_FOUND, request, exception);
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleRateLimitExceeded(
            RateLimitExceededException exception,
            HttpServletRequest request
    ) {
        logHandledException(HttpStatus.TOO_MANY_REQUESTS, request, exception);
        return build(HttpStatus.TOO_MANY_REQUESTS, "RATE_LIMIT_EXCEEDED", exception.getMessage(), request, List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiErrorResponse> handleResponseStatus(
            ResponseStatusException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        logHandledException(status, request, exception);
        return build(status, status.name(), exception.getReason(), request, List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(
            Exception exception,
            HttpServletRequest request
    ) {
        LOGGER.error(
                "unhandled_exception method={} path={} type={} message={}",
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                exception
        );
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", exception.getMessage(), request, List.of());
    }

    private void logHandledException(HttpStatus status, HttpServletRequest request, Exception exception) {
        LOGGER.warn(
                "handled_exception status={} method={} path={} type={} message={}",
                status.value(),
                request.getMethod(),
                request.getRequestURI(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );
    }

    private ApiErrorResponse.FieldViolation mapFieldError(FieldError fieldError) {
        return new ApiErrorResponse.FieldViolation(fieldError.getField(), fieldError.getDefaultMessage());
    }

    private ResponseEntity<ApiErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            HttpServletRequest request,
            List<ApiErrorResponse.FieldViolation> violations
    ) {
        ApiErrorResponse response = new ApiErrorResponse(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI(),
                violations
        );
        return ResponseEntity.status(status).body(response);
    }
}
