package ru.ilya.power.creditmanager.exception;

public class LoanAlreadyClosedException extends RuntimeException {

    public LoanAlreadyClosedException(Long loanId) {
        super("Loan is already closed: " + loanId);
    }
}