package com.morenkov.exception;

public class InvalidParametersException extends Exception {

    public InvalidParametersException() {
        super();
    }

    public InvalidParametersException(String message) {
        super(message);
    }

    public InvalidParametersException(Exception e) {
        super(e);
    }
}
