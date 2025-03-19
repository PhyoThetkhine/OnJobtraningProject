
package com.prj.LoneHPManagement.Controller;

import com.prj.LoneHPManagement.model.dto.ApiResponse;
import com.prj.LoneHPManagement.model.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ApiResponse<?>> handleServiceException(ServiceException ex) {
        ApiResponse<?> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFoundException(ServiceException ex) {
        ApiResponse<?> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAccountNotFoundException(ServiceException ex) {
        ApiResponse<?> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
    @ExceptionHandler(CifNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCIFNotFoundException(ServiceException ex) {
        ApiResponse<?> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
    @ExceptionHandler(AddressNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleAddressNotFoundException(ServiceException ex) {
        ApiResponse<?> response = ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
        return ResponseEntity.internalServerError().body(response);
    }
}


