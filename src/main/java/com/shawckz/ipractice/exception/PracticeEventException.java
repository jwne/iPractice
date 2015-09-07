package com.shawckz.ipractice.exception;

public class PracticeEventException extends PracticeException {

    public PracticeEventException() {
    }

    public PracticeEventException(String message) {
        super(message);
    }

    public PracticeEventException(String message, Throwable cause) {
        super(message, cause);
    }

    public PracticeEventException(Throwable cause) {
        super(cause);
    }

    public PracticeEventException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
