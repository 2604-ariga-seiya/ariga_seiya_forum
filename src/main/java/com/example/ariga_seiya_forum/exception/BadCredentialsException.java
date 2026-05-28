package com.example.ariga_seiya_forum.exception;

/**
 * ログイン時のアカウント名またはパスワード不一致を表す例外クラス
 */
public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException(String message) {
        super(message);
    }
}