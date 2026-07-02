package ru.ilya.power.creditmanager.exception;

public class ClientNotActiveException  extends RuntimeException {

    public ClientNotActiveException(Long id) {
        super("Client is not ACTIVE, cannot create loan: " + id);
    }
}
