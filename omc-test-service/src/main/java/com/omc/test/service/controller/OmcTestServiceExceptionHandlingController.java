package com.omc.test.service.controller;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.omc.service.exception.OmcRequestQueueFullException;

@ControllerAdvice
public class OmcTestServiceExceptionHandlingController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> invalidInput(MethodArgumentNotValidException ex) {
        BindingResult result = ex.getBindingResult();
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Validation Error");
        response.setErrorMessage("Invalid inputs.");
        response.setErrors(result.getAllErrors().stream().map(error -> error.getDefaultMessage()).collect(Collectors.toList()));
        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OmcRequestQueueFullException.class)
    public ResponseEntity<ExceptionResponse> requestIsFull(OmcRequestQueueFullException ex) {
        ExceptionResponse response = new ExceptionResponse();
        response.setErrorCode("Service Error");
        response.setErrorMessage(ex.getMessage());
        response.setErrors(Arrays.asList(ex.getMessage()));
        return new ResponseEntity<ExceptionResponse>(response, HttpStatus.SERVICE_UNAVAILABLE);
    }
    

    public class ExceptionResponse {
        private String errorCode;
        private String errorMessage;
        private List<String> errors;
		public String getErrorCode() {
			return errorCode;
		}
		public String getErrorMessage() {
			return errorMessage;
		}
		public List<String> getErrors() {
			return errors;
		}
		public void setErrorCode(String errorCode) {
			this.errorCode = errorCode;
		}
		public void setErrorMessage(String errorMessage) {
			this.errorMessage = errorMessage;
		}
		public void setErrors(List<String> errors) {
			this.errors = errors;
		}
    }
}
