package ru.ilya.power.creditmanager.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ilya.power.creditmanager.dto.loan.CreateLoanRequest;
import ru.ilya.power.creditmanager.dto.loan.LoanDto;
import ru.ilya.power.creditmanager.dto.loan.UpdateLoanRequest;
import ru.ilya.power.creditmanager.entity.*;
import ru.ilya.power.creditmanager.exception.*;
import ru.ilya.power.creditmanager.mapper.LoanMapper;
import ru.ilya.power.creditmanager.repository.ClientRepository;
import ru.ilya.power.creditmanager.repository.CurrencyRepository;
import ru.ilya.power.creditmanager.repository.LoanRepository;
import ru.ilya.power.creditmanager.repository.LoanStatusRepository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    private static final Long ACTIVE_CLIENT_STATUS_ID = 1L;
    private static final Long CLOSED_CLIENT_STATUS_ID = 3L;
    private static final Long DRAFT_LOAN_STATUS_ID = 1L;
    private static final Long ACTIVE_LOAN_STATUS_ID = 2L;
    private static final Long CLOSED_LOAN_STATUS_ID = 3L;
    private static final Long RUB_CURRENCY_ID = 1L;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private CurrencyRepository currencyRepository;

    @Mock
    private LoanStatusRepository loanStatusRepository;

    @Mock
    private LoanMapper loanMapper;

    @InjectMocks
    private LoanService loanService;

    @Test
    void createLoan_success() {
        ClientStatus activeStatus = new ClientStatus();
        activeStatus.setId(ACTIVE_CLIENT_STATUS_ID);

        Client client = new Client();
        client.setId(1L);
        client.setStatus(activeStatus);

        CreateLoanRequest request = new CreateLoanRequest();
        request.setLoanNumber("TT-TEST-001");
        request.setCurrencyId(RUB_CURRENCY_ID);
        request.setAmount(BigInteger.valueOf(100000));
        request.setInterestRate(BigDecimal.valueOf(12.5));
        request.setTermMonths(12);
        request.setMonthlyPayment(BigDecimal.valueOf(9000));

        Currency currency = new Currency();
        currency.setId(RUB_CURRENCY_ID);
        currency.setCode("RUB");

        LoanStatus draftStatus = new LoanStatus();
        draftStatus.setId(DRAFT_LOAN_STATUS_ID);

        Loan savedLoan = new Loan();
        LoanDto expectedDto = new LoanDto();

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(loanRepository.existsByLoanNumber("TT-TEST-001")).thenReturn(false);
        when(currencyRepository.findById(RUB_CURRENCY_ID)).thenReturn(Optional.of(currency));
        when(loanStatusRepository.findById(DRAFT_LOAN_STATUS_ID)).thenReturn(Optional.of(draftStatus));
        when(loanMapper.toEntity(request)).thenReturn(savedLoan);
        when(loanRepository.save(any())).thenReturn(savedLoan);
        when(loanMapper.toDto(savedLoan)).thenReturn(expectedDto);

        LoanDto result = loanService.createLoan(1L, request);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void createLoan_clientNotFound() {
        when(clientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.createLoan(99L, new CreateLoanRequest()))
                .isInstanceOf(ClientNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void createLoan_clientNotActive() {
        ClientStatus blockedStatus = new ClientStatus();
        blockedStatus.setId(2L);

        Client client = new Client();
        client.setId(1L);
        client.setStatus(blockedStatus);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));

        assertThatThrownBy(() -> loanService.createLoan(1L, new CreateLoanRequest()))
                .isInstanceOf(ClientNotActiveException.class)
                .hasMessageContaining("1");
    }

    @Test
    void createLoan_duplicateLoanNumber() {
        ClientStatus activeStatus = new ClientStatus();
        activeStatus.setId(ACTIVE_CLIENT_STATUS_ID);

        Client client = new Client();
        client.setStatus(activeStatus);

        CreateLoanRequest request = new CreateLoanRequest();
        request.setLoanNumber("LN-2024-001");

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(loanRepository.existsByLoanNumber("LN-2024-001")).thenReturn(true);

        assertThatThrownBy(() -> loanService.createLoan(1L, request))
                .isInstanceOf(DuplicateLoanNumberException.class)
                .hasMessageContaining("LN-2024-001");
    }

    @Test
    void createLoan_currencyNotFound() {
        ClientStatus activeStatus = new ClientStatus();
        activeStatus.setId(ACTIVE_CLIENT_STATUS_ID);

        Client client = new Client();
        client.setId(1L);
        client.setStatus(activeStatus);

        CreateLoanRequest request = new CreateLoanRequest();
        request.setLoanNumber("TT-TEST-001");
        request.setCurrencyId(999L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(loanRepository.existsByLoanNumber("TT-TEST-001")).thenReturn(false);
        when(currencyRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.createLoan(1L, request))
                .isInstanceOf(CurrencyNotFoundException.class);
    }

    @Test
    void updateLoan_success() {
        LoanStatus activeStatus = new LoanStatus();
        activeStatus.setId(ACTIVE_LOAN_STATUS_ID);
        activeStatus.setName("ACTIVE");

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmount(BigInteger.valueOf(100000));

        UpdateLoanRequest request = new UpdateLoanRequest();
        request.setAmount(BigInteger.valueOf(200000));
        request.setInterestRate(BigDecimal.valueOf(15.0));
        request.setStatusId(ACTIVE_LOAN_STATUS_ID);

        LoanDto expectedDto = new LoanDto();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanStatusRepository.findById(ACTIVE_LOAN_STATUS_ID)).thenReturn(Optional.of(activeStatus));
        when(loanRepository.save(any())).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(expectedDto);

        LoanDto result = loanService.updateLoan(1L, request);

        assertThat(result).isEqualTo(expectedDto);
    }

    @Test
    void updateLoan_loanNotFound() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.updateLoan(99L, new UpdateLoanRequest()))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void updateLoan_unknownStatus() {
        Loan loan = new Loan();
        loan.setId(1L);

        UpdateLoanRequest request = new UpdateLoanRequest();
        request.setStatusId(999L);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanStatusRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.updateLoan(1L, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("999");
    }

    @Test
    void updateLoan_partialUpdate_onlyAmount() {
        Loan loan = new Loan();
        loan.setId(1L);
        loan.setAmount(BigInteger.valueOf(100000));

        UpdateLoanRequest request = new UpdateLoanRequest();
        request.setAmount(BigInteger.valueOf(300000));

        LoanDto expectedDto = new LoanDto();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanRepository.save(any())).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(expectedDto);

        LoanDto result = loanService.updateLoan(1L, request);

        assertThat(result).isEqualTo(expectedDto);
        assertThat(loan.getAmount()).isEqualTo(BigInteger.valueOf(300000));
    }

    @Test
    void closeLoan_success() {
        ClientStatus activeClientStatus = new ClientStatus();
        activeClientStatus.setId(ACTIVE_CLIENT_STATUS_ID);

        Client client = new Client();
        client.setId(1L);
        client.setStatus(activeClientStatus);

        LoanStatus activeStatus = new LoanStatus();
        activeStatus.setId(ACTIVE_LOAN_STATUS_ID);

        LoanStatus closedStatus = new LoanStatus();
        closedStatus.setId(CLOSED_LOAN_STATUS_ID);
        closedStatus.setName("CLOSED");

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setClient(client);
        loan.setStatus(activeStatus);

        LoanDto expectedDto = new LoanDto();

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
        when(loanStatusRepository.findById(CLOSED_LOAN_STATUS_ID)).thenReturn(Optional.of(closedStatus));
        when(loanRepository.save(any())).thenReturn(loan);
        when(loanMapper.toDto(loan)).thenReturn(expectedDto);

        LoanDto result = loanService.closeLoan(1L);

        assertThat(result).isEqualTo(expectedDto);
        assertThat(loan.getStatus().getId()).isEqualTo(CLOSED_LOAN_STATUS_ID);
    }

    @Test
    void closeLoan_clientStatusClosed() {
        ClientStatus closedClientStatus = new ClientStatus();
        closedClientStatus.setId(CLOSED_CLIENT_STATUS_ID);

        Client client = new Client();
        client.setId(1L);
        client.setStatus(closedClientStatus);

        LoanStatus activeStatus = new LoanStatus();
        activeStatus.setId(ACTIVE_LOAN_STATUS_ID);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setClient(client);
        loan.setStatus(activeStatus);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.closeLoan(1L))
                .isInstanceOf(ClientStatusClosedException.class)
                .hasMessageContaining("1");
    }

    @Test
    void closeLoan_loanNotFound() {
        when(loanRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loanService.closeLoan(99L))
                .isInstanceOf(LoanNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void closeLoan_alreadyClosed() {
        ClientStatus activeClientStatus = new ClientStatus();
        activeClientStatus.setId(ACTIVE_CLIENT_STATUS_ID);

        Client client = new Client();
        client.setId(1L);
        client.setStatus(activeClientStatus);

        LoanStatus closedStatus = new LoanStatus();
        closedStatus.setId(CLOSED_LOAN_STATUS_ID);

        Loan loan = new Loan();
        loan.setId(1L);
        loan.setClient(client);
        loan.setStatus(closedStatus);

        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loanService.closeLoan(1L))
                .isInstanceOf(LoanAlreadyClosedException.class)
                .hasMessageContaining("1");
    }
}