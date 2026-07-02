package ru.ilya.power.creditmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ilya.power.creditmanager.dto.loan.CreateLoanRequest;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.entity.Client;
import ru.ilya.power.creditmanager.entity.Currency;
import ru.ilya.power.creditmanager.entity.Loan;
import ru.ilya.power.creditmanager.entity.LoanStatus;
import ru.ilya.power.creditmanager.exception.ClientNotActiveException;
import ru.ilya.power.creditmanager.exception.ClientNotFoundException;
import ru.ilya.power.creditmanager.exception.DuplicateLoanNumberException;
import ru.ilya.power.creditmanager.mapper.LoanMapper;
import ru.ilya.power.creditmanager.repository.ClientRepository;
import ru.ilya.power.creditmanager.repository.CurrencyRepository;
import ru.ilya.power.creditmanager.repository.LoanRepository;
import ru.ilya.power.creditmanager.repository.LoanStatusRepository;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final String CLIENT_STATUS_ACTIVE = "ACTIVE";
    private static final String CLIENT_STATUS_DRAFT = "DRAFT";

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;
    private final CurrencyRepository currencyRepository;
    private final LoanStatusRepository loanStatusRepository;
    private final LoanMapper loanMapper;

    public LoanDto createLoan(Long clientId, CreateLoanRequest request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        if (!CLIENT_STATUS_ACTIVE.equals(client.getStatus().getName())) {
            throw new ClientNotActiveException(clientId);
        }

        if (loanRepository.existsByLoanNumber(request.getLoanNumber())) {
            throw new DuplicateLoanNumberException(request.getLoanNumber());
        }

        Currency currency = currencyRepository.findByCode(request.getCurrency())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Unknown currency: " + request.getCurrency()));

        LoanStatus draftStatus = loanStatusRepository.findByName(CLIENT_STATUS_DRAFT)
                .orElseThrow(() -> new IllegalStateException(CLIENT_STATUS_DRAFT + " status not found"));

        Loan loan = loanMapper.toEntity(request);
        loan.setClient(client);
        loan.setCurrency(currency);
        loan.setStatus(draftStatus);

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }
}