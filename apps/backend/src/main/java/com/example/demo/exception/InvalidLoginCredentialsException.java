package com.example.demo.exception;

public class InvalidLoginCredentialsException extends RuntimeException implements CustomException {
    private final String errorCode;
    private final String errorMessage;

    public InvalidLoginCredentialsException(String message) {
        super(message);
        this.errorCode = "INVALID_LOGIN_CREDENTIALS";
        this.errorMessage = message;
    }

    public InvalidLoginCredentialsException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode != null ? errorCode : "INVALID_LOGIN_CREDENTIALS";
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
