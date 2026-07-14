package ru.ilya.power.creditmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ilya.power.creditmanager.entity.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findByLastNameContainingIgnoreCaseAndFirstNameContainingIgnoreCase(
            String lastName,
            String firstName
    );

    List<Client> findByPassportNumber(String passportNumber);

    boolean existsByPassportNumber(String passportNumber);

    boolean existsByEmail(String email);
}