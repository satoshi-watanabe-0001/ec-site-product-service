package com.example.ec.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@DisplayName("GlobalExceptionHandler単体テスト")
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler exceptionHandler;

  @BeforeEach
  void setUp() {
    exceptionHandler = new GlobalExceptionHandler();
  }

  @Test
  @DisplayName("MethodArgumentNotValidException処理")
  void handleValidationExceptionReturnsValidationError() {
    BindException bindException = new BindException(new Object(), "request");
    bindException.addError(new FieldError("request", "page", "ページ番号は0以上である必要があります"));

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleBindException(bindException);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
    assertThat(response.getBody().getMessage()).isEqualTo("入力値の検証に失敗しました");
    assertThat(response.getBody().getFieldErrors()).isNotNull();
    assertThat(response.getBody().getFieldErrors()).containsKey("page");
    assertThat(response.getBody().getFieldErrors().get("page")).isEqualTo("ページ番号は0以上である必要があります");
    assertThat(response.getBody().getTimestamp()).isNotNull();
    assertThat(response.getBody().getRequestId()).isNotNull();
  }

  @Test
  @DisplayName("BindException処理")
  void handleBindExceptionReturnsValidationError() {
    BindException bindException = new BindException(new Object(), "request");
    bindException.addError(new FieldError("request", "sort", "不正なソート項目です"));

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleBindException(bindException);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
    assertThat(response.getBody().getMessage()).isEqualTo("入力値の検証に失敗しました");
    assertThat(response.getBody().getFieldErrors()).containsKey("sort");
    assertThat(response.getBody().getFieldErrors().get("sort")).isEqualTo("不正なソート項目です");
  }

  @Test
  @DisplayName("MethodArgumentTypeMismatchException処理")
  void handleTypeMismatchReturnsTypeMismatchError() {
    MethodArgumentTypeMismatchException exception = mock(MethodArgumentTypeMismatchException.class);
    when(exception.getName()).thenReturn("categoryId");
    when(exception.getValue()).thenReturn("invalid");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("TYPE_MISMATCH");
    assertThat(response.getBody().getMessage())
        .contains("categoryId")
        .contains("invalid")
        .contains("不正です");
    assertThat(response.getBody().getTimestamp()).isNotNull();
    assertThat(response.getBody().getRequestId()).isNotNull();
  }

  @Test
  @DisplayName("IllegalArgumentException処理")
  void handleIllegalArgumentExceptionReturnsInvalidArgumentError() {
    IllegalArgumentException exception = new IllegalArgumentException("不正な引数が指定されました");

    ResponseEntity<ErrorResponse> response =
        exceptionHandler.handleIllegalArgumentException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("INVALID_ARGUMENT");
    assertThat(response.getBody().getMessage()).isEqualTo("不正な引数が指定されました");
    assertThat(response.getBody().getTimestamp()).isNotNull();
    assertThat(response.getBody().getRequestId()).isNotNull();
  }

  @Test
  @DisplayName("Exception処理（予期しないエラー）")
  void handleGenericExceptionReturnsInternalServerError() {
    Exception exception = new RuntimeException("予期しないエラーが発生しました");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
    assertThat(response.getBody().getMessage()).isEqualTo("予期しないエラーが発生しました");
    assertThat(response.getBody().getTimestamp()).isNotNull();
    assertThat(response.getBody().getRequestId()).isNotNull();
  }

  @Test
  @DisplayName("複数フィールドエラーの処理")
  void handleValidationExceptionWithMultipleFieldErrorsReturnsAllErrors() {
    BindException bindException = new BindException(new Object(), "request");
    bindException.addError(new FieldError("request", "page", "ページ番号は0以上である必要があります"));
    bindException.addError(new FieldError("request", "size", "サイズは1以上100以下である必要があります"));

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleBindException(bindException);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getFieldErrors()).hasSize(2);
    assertThat(response.getBody().getFieldErrors()).containsKeys("page", "size");
  }

  @Test
  @DisplayName("NullPointerException処理（予期しないエラーとして扱う）")
  void handleGenericExceptionWithNullPointerExceptionReturnsInternalServerError() {
    NullPointerException exception = new NullPointerException("Null pointer error");

    ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
  }
}
