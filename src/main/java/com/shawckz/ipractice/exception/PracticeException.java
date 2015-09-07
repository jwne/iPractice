package com.shawckz.ipractice.exception;

public class PracticeException extends RuntimeException {

    public PracticeException() {
    }

    public PracticeException(String message) {
        super(message);
    }

    public PracticeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PracticeException(Throwable cause) {
        super(cause);
    }

    public PracticeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
