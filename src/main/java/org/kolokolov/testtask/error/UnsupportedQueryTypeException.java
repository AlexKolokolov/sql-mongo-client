package org.kolokolov.testtask.error;

public class UnsupportedQueryTypeException extends RuntimeException {
    public UnsupportedQueryTypeException(String message) {
        super(message);
    }
}
