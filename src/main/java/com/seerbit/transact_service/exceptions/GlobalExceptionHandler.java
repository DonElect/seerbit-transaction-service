package com.seerbit.transact_service.exceptions;


import com.seerbit.transact_service.model.ApiResponse;
import com.seerbit.transact_service.model.ErrorDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OldTransactionException.class)
    public ResponseEntity<ApiResponse> handleOldTransactionException(final OldTransactionException exception){
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception){
        ErrorDetail errorDetail = ErrorDetail.builder()
                .message("Malformed JSON request")
                .status(HttpStatus.BAD_REQUEST)
                .build();
        ApiResponse response = new ApiResponse("400", exception.getMessage(), errorDetail);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnprocessableEntityException.class)
    public ResponseEntity<ApiResponse> handleUnprocessableEntityException(final UnprocessableEntityException exception){
        return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidExceptionException(MethodArgumentNotValidException exception) {
        String errorMessage = "Request validation failure. Please check your request data.";
        BindingResult result = exception.getBindingResult();
        FieldError fieldError = result.getFieldError();
        if(fieldError != null) {
            errorMessage = fieldError.getDefaultMessage();
        }
        ApiResponse apiResponse = new ApiResponse("422", errorMessage, null);
        return new ResponseEntity<>(apiResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
