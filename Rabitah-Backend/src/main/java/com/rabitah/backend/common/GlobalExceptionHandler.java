package com.rabitah.backend.common;
import org.springframework.http.*; import org.springframework.web.bind.MethodArgumentNotValidException; import org.springframework.web.bind.annotation.*; import java.time.Instant; import java.util.*;
@RestControllerAdvice public class GlobalExceptionHandler {
 @ExceptionHandler(ApiException.class) ResponseEntity<ApiError> api(ApiException e){return ResponseEntity.status(e.status()).body(new ApiError(Instant.now(),e.status().value(),e.code(),e.getMessage(),Map.of()));}
 @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException e){Map<String,String> f=new LinkedHashMap<>();e.getBindingResult().getFieldErrors().forEach(x->f.putIfAbsent(x.getField(),x.getDefaultMessage()));return ResponseEntity.badRequest().body(new ApiError(Instant.now(),400,"VALIDATION_FAILED","Request validation failed",f));}
}
