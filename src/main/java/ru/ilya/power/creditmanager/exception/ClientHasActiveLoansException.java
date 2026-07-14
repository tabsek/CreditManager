package ru.ilya.power.creditmanager.exception;

public class ClientHasActiveLoansException extends RuntimeException {

    public ClientHasActiveLoansException(Long clientId) {
        super("Client with id " + clientId + " has active loans and cannot be blocked");
    }
}