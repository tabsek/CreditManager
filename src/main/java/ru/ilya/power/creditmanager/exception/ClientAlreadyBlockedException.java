package ru.ilya.power.creditmanager.exception;

public class ClientAlreadyBlockedException extends RuntimeException {

    public ClientAlreadyBlockedException(Long clientId) {
        super("Client with id " + clientId + " is already blocked");
    }
}