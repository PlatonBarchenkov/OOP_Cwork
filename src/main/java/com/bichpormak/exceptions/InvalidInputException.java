package com.bichpormak.exceptions;

/**
 * Исключение, выбрасываемое при неверном вводе данных.
 */
public class InvalidInputException extends Exception {
    public InvalidInputException(String message) {
        super(message);
    }
}
