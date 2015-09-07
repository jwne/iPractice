package com.shawckz.ipractice.exception;

public class PartyException extends PracticeException {

    public PartyException() {
    }

    public PartyException(String message) {
        super(message);
    }

    public PartyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PartyException(Throwable cause) {
        super(cause);
    }

    public PartyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
