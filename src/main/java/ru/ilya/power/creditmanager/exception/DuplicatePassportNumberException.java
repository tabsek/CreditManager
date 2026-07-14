package ru.ilya.power.creditmanager.exception;

public class DuplicatePassportNumberException extends RuntimeException {

    public DuplicatePassportNumberException(String passportNumber) {
        super("Passport number already exists: " + passportNumber);
    }
}