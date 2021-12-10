package com.sprect.exception;

public class RightEditException extends RuntimeException {

    public RightEditException(String message) {
        super(message);
    }

    public RightEditException(String message, Throwable cause) {
        super(message, cause);
    }
}
