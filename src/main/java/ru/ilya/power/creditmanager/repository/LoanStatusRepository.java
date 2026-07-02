package ru.ilya.power.creditmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ilya.power.creditmanager.entity.LoanStatus;

import java.util.Optional;

public interface LoanStatusRepository extends JpaRepository<LoanStatus, Long> {

    Optional<LoanStatus> findByName(String name);
}