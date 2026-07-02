package ru.ilya.power.creditmanager.exception;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(Long id) {
        super("Loan not found: " + id);
    }
}