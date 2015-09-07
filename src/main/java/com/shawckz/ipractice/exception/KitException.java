package com.shawckz.ipractice.exception;

public class KitException extends PracticeException {

    public KitException() {
    }

    public KitException(String message) {
        super(message);
    }

    public KitException(String message, Throwable cause) {
        super(message, cause);
    }

    public KitException(Throwable cause) {
        super(cause);
    }

    public KitException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
