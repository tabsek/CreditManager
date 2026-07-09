package ru.ilya.power.creditmanager.exception;

public class CurrencyNotFoundException extends RuntimeException{
    public CurrencyNotFoundException (Long currencyId) {
        super("Currency with id " + currencyId + " not found");
    }
}
