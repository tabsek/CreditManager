package ru.ilya.power.creditmanager.exception;

public class LoanAlreadyClosedException extends RuntimeException {

    public LoanAlreadyClosedException(Long id) {
        super("Loan is already closed: " + id);
    }
}