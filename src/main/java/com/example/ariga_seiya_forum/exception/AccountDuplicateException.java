package com.example.ariga_seiya_forum.exception;

public class AccountDuplicateException extends RuntimeException {

    public AccountDuplicateException(String message) {
        super(message);
    }
}