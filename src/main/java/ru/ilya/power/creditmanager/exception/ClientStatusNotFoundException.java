package ru.ilya.power.creditmanager.exception;

public class ClientStatusNotFoundException extends RuntimeException {

    public ClientStatusNotFoundException(Long id) {
        super("Client status not found: " + id);
    }
}