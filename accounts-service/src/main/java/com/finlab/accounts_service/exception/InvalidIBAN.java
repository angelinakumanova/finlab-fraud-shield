package com.finlab.accounts_service.exception;

public class InvalidIBAN extends RuntimeException {
    public InvalidIBAN(String message) {
        super(message);
    }
}
