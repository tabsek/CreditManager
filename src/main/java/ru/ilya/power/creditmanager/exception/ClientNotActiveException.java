package ru.ilya.power.creditmanager.exception;

public class ClientNotActiveException  extends RuntimeException {

    public ClientNotActiveException(Long clientId) {
        super("Client is not ACTIVE, cannot create loan: " + clientId);
    }
}
