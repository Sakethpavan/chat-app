package com.example.demo.exception;

public class UserAlreadyExistsException extends RuntimeException implements CustomException {

    private final String errorCode;
    private final String errorMessage;

    public UserAlreadyExistsException(String message) {
        super(message);
        this.errorCode = "USER_ALREADY_EXISTS";
        this.errorMessage = message;
    }

    public UserAlreadyExistsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : "USER_ALREADY_EXISTS";
        this.errorMessage = message;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }
}
