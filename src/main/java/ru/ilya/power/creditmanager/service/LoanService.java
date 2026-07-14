package ru.ilya.power.creditmanager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

    private static final Long ACTIVE_CLIENT_STATUS_ID = 1L;
    private static final Long CLOSED_CLIENT_STATUS_ID = 3L;
    private static final Long DRAFT_LOAN_STATUS_ID = 1L;
    private static final Long CLOSED_LOAN_STATUS_ID = 3L;

    private final LoanRepository loanRepository;
    private final ClientRepository clientRepository;
    private final CurrencyRepository currencyRepository;
    private final LoanStatusRepository loanStatusRepository;
    private final LoanMapper loanMapper;

    @Transactional
    public LoanDto createLoan(Long clientId, CreateLoanRequest request) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        if (!ACTIVE_CLIENT_STATUS_ID.equals(client.getStatus().getId())) {
            throw new ClientNotActiveException(clientId);
        }

        if (loanRepository.existsByLoanNumber(request.getLoanNumber())) {
            throw new DuplicateLoanNumberException(request.getLoanNumber());
        }

        Currency currency = currencyRepository.findById(request.getCurrencyId())
                .orElseThrow(() -> new CurrencyNotFoundException(request.getCurrencyId()));

        LoanStatus draftStatus = loanStatusRepository.findById(DRAFT_LOAN_STATUS_ID)
                .orElseThrow(() -> new LoanStatusNotFoundException(DRAFT_LOAN_STATUS_ID));

        Loan loan = loanMapper.toEntity(request);
        loan.setClient(client);
        loan.setCurrency(currency);
        loan.setStatus(draftStatus);

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }

    @Transactional
    public LoanDto updateLoan(Long loanId, UpdateLoanRequest request) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        Client client = loan.getClient();

        if (CLOSED_CLIENT_STATUS_ID.equals(client.getStatus().getId())) {
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

    @Transactional
    public LoanDto closeLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(loanId));

        Client client = loan.getClient();

        if (CLOSED_CLIENT_STATUS_ID.equals(client.getStatus().getId())) {
            throw new ClientStatusClosedException(client.getId());
        }

        if (CLOSED_LOAN_STATUS_ID.equals(loan.getStatus().getId())) {
            throw new LoanAlreadyClosedException(loanId);
        }

        LoanStatus closedStatus = loanStatusRepository.findById(CLOSED_LOAN_STATUS_ID)
                .orElseThrow(() -> new LoanStatusNotFoundException(CLOSED_LOAN_STATUS_ID));

        loan.setStatus(closedStatus);

        Loan savedLoan = loanRepository.save(loan);
        return loanMapper.toDto(savedLoan);
    }
}