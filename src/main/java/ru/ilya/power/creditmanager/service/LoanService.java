package ru.ilya.power.creditmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.ilya.power.creditmanager.dto.loan.CreateLoanRequest;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.dto.loan.UpdateLoanRequest;
import ru.ilya.power.creditmanager.entity.Client;
import ru.ilya.power.creditmanager.entity.Currency;
import ru.ilya.power.creditmanager.entity.Loan;
import ru.ilya.power.creditmanager.entity.LoanStatus;
import ru.ilya.power.creditmanager.exception.*;
import ru.ilya.power.creditmanager.mapper.LoanMapper;
import ru.ilya.power.creditmanager.repository.ClientRepository;
import ru.ilya.power.creditmanager.repository.CurrencyRepository;
import ru.ilya.power.creditmanager.repository.LoanRepository;
import ru.ilya.power.creditmanager.repository.LoanStatusRepository;

@Service
@RequiredArgsConstructor
public class LoanService {

    private static final Long CLIENT_ID_STATUS_ACTIVE = 1L;
    private static final Long CLIENT_ID_STATUS_CLOSED = 3L;
    private static final Long LOAN_ID_STATUS_DRAFT = 1L;
    private static final Long LOAN_ID_STATUS_CLOSED = 3L;

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;
    private final CurrencyRepository currencyRepository;
    private final LoanStatusRepository loanStatusRepository;
    private final LoanMapper loanMapper;

    public LoanDto createLoan(Long clientId, CreateLoanRequest request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        if (!CLIENT_ID_STATUS_ACTIVE.equals(client.getStatus().getId())) {
            throw new ClientNotActiveException(clientId);
        }

        if (loanRepository.existsByLoanNumber(request.getLoanNumber())) {
            throw new DuplicateLoanNumberException(request.getLoanNumber());
        }

        Currency currency = currencyRepository.findById(request.getCurrencyId())
                .orElseThrow(() -> new CurrencyNotFoundException(request.getCurrencyId()));

        LoanStatus draftStatus = loanStatusRepository.findById(LOAN_ID_STATUS_DRAFT)
                .orElseThrow(() -> new LoanStatusNotFoundException(LOAN_ID_STATUS_DRAFT));

        Loan loan = loanMapper.toEntity(request);
        loan.setClient(client);
        loan.setCurrency(currency);
        loan.setStatus(draftStatus);

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    public LoanDto updateLoan(Long loanId, UpdateLoanRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        Client client = loan.getClient();

        if (CLIENT_ID_STATUS_CLOSED.equals(client.getStatus().getId())) {
            throw new ClientStatusClosedException(client.getId());
        }

        if (request.getAmount() != null) {
            loan.setAmount(request.getAmount());
        }

        if (request.getInterestRate() != null) {
            loan.setInterestRate(request.getInterestRate());
        }

        if (request.getStatusId() != null) {
            LoanStatus loanStatus = loanStatusRepository.findById(request.getStatusId())
                    .orElseThrow(() -> new LoanStatusNotFoundException(request.getStatusId()));
            loan.setStatus(loanStatus);
        }

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    public LoanDto closeLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        Client client = loan.getClient();

        if (CLIENT_ID_STATUS_CLOSED.equals(client.getStatus().getId())) {
            throw new ClientStatusClosedException(client.getId());
        }

        if (LOAN_ID_STATUS_CLOSED.equals(loan.getStatus().getId())) {
            throw new LoanAlreadyClosedException(loanId);
        }

        LoanStatus closedStatus = loanStatusRepository.findById(LOAN_ID_STATUS_CLOSED)
                .orElseThrow(() -> new LoanStatusNotFoundException(LOAN_ID_STATUS_CLOSED));

        loan.setStatus(closedStatus);

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }
}