package ru.ilya.power.creditmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ilya.power.creditmanager.entity.ClientStatus;

public interface ClientStatusRepository extends JpaRepository<ClientStatus, Long> {
}