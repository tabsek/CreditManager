package ru.ilya.power.creditmanager.exception;

public class LoanNotFoundException extends RuntimeException {

    public LoanNotFoundException(Long loanId) {
        super("Loan not found: " + loanId);
    }
}