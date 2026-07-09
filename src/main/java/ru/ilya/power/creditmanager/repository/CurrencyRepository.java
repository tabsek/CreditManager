package ru.ilya.power.creditmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ilya.power.creditmanager.entity.Currency;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Optional<Currency> findById(Long id);
}
