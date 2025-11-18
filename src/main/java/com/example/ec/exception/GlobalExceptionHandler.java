package com.example.ec.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * グローバル例外ハンドラー
 *
 * <p>アプリケーション全体の例外を統一的に処理するハンドラークラス。 組織標準のエラーレスポンス形式に準拠。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * バリデーションエラーのハンドリング
   *
   * @param ex バリデーション例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {

    log.warn("Validation error occurred: {}", ex.getMessage());

    Map<String, String> fieldErrors = new HashMap<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      fieldErrors.put(error.getField(), error.getDefaultMessage());
    }

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .success(false)
            .errorCode("VALIDATION_ERROR")
            .message("入力値の検証に失敗しました")
            .fieldErrors(fieldErrors)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * 不正な引数エラーのハンドリング
   *
   * @param ex 不正な引数例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {

    log.warn("Illegal argument error occurred: {}", ex.getMessage());

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .success(false)
            .errorCode("INVALID_ARGUMENT")
            .message(ex.getMessage())
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  /**
   * 予期しないエラーのハンドリング
   *
   * @param ex 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {

    log.error("Unexpected error occurred", ex);

    ErrorResponse errorResponse =
        ErrorResponse.builder()
            .success(false)
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("予期しないエラーが発生しました")
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
