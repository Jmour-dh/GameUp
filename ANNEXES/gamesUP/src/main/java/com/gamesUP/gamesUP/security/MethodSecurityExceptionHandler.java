// java
package com.gamesUP.gamesUP.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodSecurityExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        Map<String, Object> body = Map.of(
                "status", HttpStatus.FORBIDDEN.value(),
                "error", "Forbidden",
                "message", ex.getMessage() != null ? ex.getMessage() : "Accès refusé : rôle ADMIN requis",
                "path", request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleWrappedAccessDenied(Exception ex, HttpServletRequest request) {
        AccessDeniedException ade = findAccessDenied(ex);
        if (ade != null) {
            Map<String, Object> body = Map.of(
                    "status", HttpStatus.FORBIDDEN.value(),
                    "error", "Forbidden",
                    "message", ade.getMessage() != null ? ade.getMessage() : "Accès refusé : rôle ADMIN requis",
                    "path", request.getRequestURI()
            );
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
        }
        Map<String, Object> body = Map.of(
                "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "error", "Internal Server Error",
                "message", ex.getMessage() != null ? ex.getMessage() : "Une erreur inattendue est survenue",
                "path", request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private AccessDeniedException findAccessDenied(Throwable t) {
        while (t != null) {
            if (t instanceof AccessDeniedException) {
                return (AccessDeniedException) t;
            }
            t = t.getCause();
        }
        return null;
    }
}
