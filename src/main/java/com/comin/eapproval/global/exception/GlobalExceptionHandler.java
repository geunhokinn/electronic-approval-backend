package com.comin.eapproval.global.exception;

import com.comin.eapproval.global.exception.attachment.AttachmentErrorCode;
import com.comin.eapproval.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException exception
    ) {
        ErrorCode errorCode = exception.getErrorCode();

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getFieldErrors()
                .getFirst()
                .getDefaultMessage();

        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(message));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded() {
        AttachmentErrorCode errorCode =
                AttachmentErrorCode.ATTACHMENT_SIZE_EXCEEDED;

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(ApiResponse.fail(errorCode.getMessage()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(
            MissingServletRequestParameterException exception
    ) {
        return ResponseEntity.badRequest()
                .body(
                        ApiResponse.fail(
                                exception.getParameterName() + " 파라미터는 필수입니다."
                        )
                );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(
            MethodArgumentTypeMismatchException exception
    ) {
        return ResponseEntity.badRequest()
                .body(
                        ApiResponse.fail(
                                exception.getName() + " 값이 올바르지 않습니다."
                        )
                );
    }

    /**
     * JSON 또는 multipart 요청 본문을 DTO로 변환하지 못한 경우를 처리합니다.
     *
     * @param exception 요청 본문 변환 예외
     * @return 사용자에게 표시할 잘못된 요청 응답
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleMessageNotReadable(
            HttpMessageNotReadableException exception
    ) {
        String message = containsDateParseException(exception)
                ? "시행일을 실제 날짜로 입력해주세요."
                : "요청 형식이 올바르지 않습니다.";

        return ResponseEntity.badRequest().body(ApiResponse.fail(message));
    }

    /**
     * 요청 본문 변환 예외의 원인에 날짜 파싱 오류가 포함되어 있는지 확인합니다.
     *
     * @param exception 요청 본문 변환 예외
     * @return 날짜 파싱 오류 포함 여부
     */
    private boolean containsDateParseException(Throwable exception) {
        Throwable cause = exception;

        while (cause != null) {
            if (cause instanceof DateTimeParseException) {
                return true;
            }

            cause = cause.getCause();
        }

        return false;
    }
}
