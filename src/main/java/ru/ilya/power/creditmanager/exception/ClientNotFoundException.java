package ru.ilya.power.creditmanager.exception;

public class ClientNotFoundException extends RuntimeException {

    public ClientNotFoundException(Long clientId) {
        super("Client not found: " + clientId);
    }
}
