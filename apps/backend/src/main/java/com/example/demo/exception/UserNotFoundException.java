package com.example.demo.exception;

public class UserNotFoundException extends RuntimeException implements CustomException {

    private final String errorCode;
    private final String errorMessage;

    public UserNotFoundException(String message) {
        super(message);
        this.errorCode = "USER_NOT_FOUND";
        this.errorMessage = message;
    }

    public UserNotFoundException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : "USER_NOT_FOUND";  // Use default if null
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
