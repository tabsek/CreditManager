package ru.ilya.power.creditmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.ilya.power.creditmanager.entity.Loan;

import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {

    boolean existsByLoanNumber(String loanNumber);

    List<Loan> findByClientId(Long clientId);
}
