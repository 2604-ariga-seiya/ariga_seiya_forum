package com.example.ariga_seiya_forum.exception;

/**
 * アカウントが停止状態であることを表す例外クラス
 */
public class DisabledException extends RuntimeException {

    public DisabledException(String message) {
        super(message);
    }
}