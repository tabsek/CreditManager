package ru.ilya.power.creditmanager.exception;

public class DuplicateLoanNumberException extends RuntimeException {

    public DuplicateLoanNumberException (String loanNumber) {
        super("Loan number already exists: " + loanNumber);
    }
}
