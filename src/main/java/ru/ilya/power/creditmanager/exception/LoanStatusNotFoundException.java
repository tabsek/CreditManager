package ru.ilya.power.creditmanager.exception;

public class LoanStatusNotFoundException extends RuntimeException{
    public LoanStatusNotFoundException (Long loanStatusId) {
        super("Loan status with id " + loanStatusId + " not found");
    }
}
