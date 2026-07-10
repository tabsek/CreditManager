package ru.ilya.power.creditmanager.exception;

public class ClientStatusClosedException extends RuntimeException{
    public ClientStatusClosedException(Long clientId) {
        super("Client with id " + clientId + " has status CLOSED");
    }
}
